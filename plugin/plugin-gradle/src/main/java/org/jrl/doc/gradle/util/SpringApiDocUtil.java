package org.jrl.doc.gradle.util;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaMethod;
import org.jrl.doc.gradle.model.ApiConfig;
import org.jrl.doc.gradle.model.doc.JrlClassDoc;
import org.jrl.doc.gradle.model.doc.JrlMethodDoc;
import org.jrl.doc.gradle.model.doc.JrlProjectDoc;
import org.jrl.doc.gradle.model.doc.JrlRootDoc;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.gradle.api.logging.Logger;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 扫描class注释信息
 * @author longjuncheng
 */
public class SpringApiDocUtil {

    /**
     * 入口
     *
     * @param apiConfig 配置信息
     * @param logger    日志对象
     */
    public static String begin(ApiConfig apiConfig, Logger logger) throws Exception {
        JrlRootDoc jrlRootDoc = new JrlRootDoc();
        jrlRootDoc.setAppName(apiConfig.getAppName());
        jrlRootDoc.setPomVersion(apiConfig.getPomVersion());
        jrlRootDoc.setProjectDocMap(new HashMap<>(apiConfig.getScannerPackageMap().size()));
        for (Map.Entry<String, String> source : apiConfig.getScannerPackageMap().entrySet()) {
            try {
                Map<String, JavaClass> javaClassMap = new ConcurrentHashMap<>(16);
                JavaProjectBuilder builder = new JavaProjectBuilder();
                builder.addSourceTree(new File(source.getValue()));
                builder.setErrorHandler((e)-> logger.warn(e.getMessage()));
                Collection<JavaClass> classes = builder.getClasses();
                for (JavaClass javaClass : classes) {
                    //排除文件, 限制文件路径
                    boolean isExclude = false;
                    for (String classPath : apiConfig.getClassPaths()) {
                        if (!javaClass.getPackageName().startsWith(classPath)) {
                            isExclude = true;
                        }
                    }
                    if (isExclude) {
                        continue;
                    }
                    //除外文件, 排除接口
                    if (!javaClass.isInterface() && !javaClass.getName().endsWith("Service")
                            && !javaClass.getFullyQualifiedName().endsWith("Mapper")
                            && !javaClass.getFullyQualifiedName().endsWith("DAO")
                            && !javaClass.getFullyQualifiedName().endsWith("Impl")
                            && !javaClass.getFullyQualifiedName().endsWith("Util")) {
                        javaClassMap.put(javaClass.getPackageName() + "." + javaClass.getName(), javaClass);
                    }
                }
                //扫描文档
                final JrlProjectDoc jrlProjectDoc = scanDoc(javaClassMap);
                if (!jrlProjectDoc.isEmpty()) {
                    jrlRootDoc.getProjectDocMap().put(source.getKey(), jrlProjectDoc);
                }
            } catch (Exception e) {
                logger.error("扫描文档异常 ! source : {}", JrlJsonUtils.toJson(source), e);
            }
        }
        //调用测试平台接口，同步
        return syncPostCall(apiConfig.getReportAddress() + "/client/api/doc/save", JrlJsonUtils.toJson(jrlRootDoc));
    }

    /**
     * 扫描文档
     *
     * @param javaClassMap
     */
    private static JrlProjectDoc scanDoc(Map<String, JavaClass> javaClassMap) {
        JrlProjectDoc jrlProjectDoc = new JrlProjectDoc();
        jrlProjectDoc.setClassDocMap(new ConcurrentHashMap<>(1024));
        for (Map.Entry<String, JavaClass> m : javaClassMap.entrySet()) {
            buildClassDoc(jrlProjectDoc, m);
        }
        return jrlProjectDoc;
    }

    private static void buildClassDoc(JrlProjectDoc jrlProjectDoc, Map.Entry<String, JavaClass> m) {
        JrlClassDoc jrlClassDoc = new JrlClassDoc();
        jrlClassDoc.setFiledDocMap(new HashMap<>(8));
        jrlClassDoc.setMethodDocMap(new HashMap<>(8));

        //开始扫描
        JavaClass javaClass = m.getValue();
        //获取类Javadoc注释信息
        final String classComment = javaClass.getComment();
        jrlClassDoc.setComment(classComment);

        //获取类所有属性的注释
        final List<JavaField> fields = javaClass.getFields();
        if (CollectionUtils.isNotEmpty(fields)) {
            for (JavaField field : fields) {
                if (StringUtils.isNotBlank(field.getComment())) {
                    jrlClassDoc.getFiledDocMap().put(field.getName(), field.getComment());
                }
            }
        }

        //获取类所有方法的注释，@param，@return
        final List<JavaMethod> methods = javaClass.getMethods();
        if (CollectionUtils.isNotEmpty(methods)) {
            methods.forEach(method -> {
                if (method.isPublic() && !method.isStatic()) {
                    final JrlMethodDoc methodDoc = getMethodDoc(method);
                    if (!methodDoc.isEmpty()) {
                        jrlClassDoc.getMethodDocMap().put(method.getName(), methodDoc);
                    }
                }
            });
        }
        if (!jrlClassDoc.isEmpty()) {
            jrlProjectDoc.getClassDocMap().put(m.getKey(), jrlClassDoc);
        }
    }

    private static JrlMethodDoc getMethodDoc(JavaMethod method) {
        final JrlMethodDoc jrlMethodDoc = new JrlMethodDoc();
        //获取方法注释
        if (StringUtils.isNotBlank(method.getComment())) {
            jrlMethodDoc.setComment(method.getComment());
        }
        //获取参数注释
        if (CollectionUtils.isNotEmpty(method.getParameters())) {
            final List<DocletTag> param = method.getTagsByName("param");
            if (CollectionUtils.isNotEmpty(param)) {
                jrlMethodDoc.setParamsDoc(new HashMap<>(param.size()));
                for (DocletTag docletTag : param) {
                    if (StringUtils.isNotBlank(docletTag.getValue())) {
                        jrlMethodDoc.getParamsDoc().put(docletTag.getName(), docletTag.getValue());
                    }
                }
            }
        }
        //获取返回注释
        final List<DocletTag> aReturn = method.getTagsByName("return");
        if (CollectionUtils.isNotEmpty(aReturn) && StringUtils.isNotBlank(aReturn.get(0).getValue())) {
            jrlMethodDoc.setReturnDoc(aReturn.get(0).getValue());
        }
        return jrlMethodDoc;
    }

    public static String syncPostCall(String url, String requestBodyJson) throws Exception {
        // 创建Httpclient对象
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        //添加请求头
        httpPost.addHeader("Content-Type", "application/json;charset=utf-8");
        // 组织数据
        StringEntity se = new StringEntity(requestBodyJson, "utf-8");

        //对于POST请求,把请求体填充进HttpPost实体.
        httpPost.setEntity(se);
        try {
            CloseableHttpResponse response = httpclient.execute(httpPost);
            //通过HttpResponse接口的getEntity方法返回响应信息，并进行相应的处理
            return EntityUtils.toString(response.getEntity());
        } finally {
            //最后关闭HttpClient资源
            httpclient.close();
        }

    }
}

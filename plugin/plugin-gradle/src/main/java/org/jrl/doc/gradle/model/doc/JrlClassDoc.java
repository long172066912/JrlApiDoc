package org.jrl.doc.gradle.model.doc;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
* @Description: //TODO (用一句话描述该文件做什么)
* @author JerryLong
*/
public class JrlClassDoc {
    /**
     * 类注释
     */
    private String comment;
    /**
     * 类属性
     */
    private Map<String, String> filedDocMap;
    /**
     * 类方法
     */
    private Map<String, JrlMethodDoc> methodDocMap;

    @JsonIgnore
    public boolean isEmpty() {
        return StringUtils.isBlank(comment) && (filedDocMap == null || filedDocMap.isEmpty()) && (methodDocMap == null || methodDocMap.isEmpty());
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Map<String, String> getFiledDocMap() {
        return filedDocMap;
    }

    public void setFiledDocMap(Map<String, String> filedDocMap) {
        this.filedDocMap = filedDocMap;
    }

    public Map<String, JrlMethodDoc> getMethodDocMap() {
        return methodDocMap;
    }

    public void setMethodDocMap(Map<String, JrlMethodDoc> methodDocMap) {
        this.methodDocMap = methodDocMap;
    }
}

package org.jrl.doc.gradle.model.doc;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
* @Description: //TODO (用一句话描述该文件做什么)
* @author JerryLong
*/
public class JrlMethodDoc {
    /**
     * 方法注释
     */
    private String comment;
    /**
     * 参数注释
     */
    private Map<String, String> paramsDoc;
    /**
     * 返回值注释
     */
    private String returnDoc;

    @JsonIgnore
    public boolean isEmpty() {
        return StringUtils.isBlank(comment) && StringUtils.isBlank(returnDoc) && (null == paramsDoc || paramsDoc.isEmpty());
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Map<String, String> getParamsDoc() {
        return paramsDoc;
    }

    public void setParamsDoc(Map<String, String> paramsDoc) {
        this.paramsDoc = paramsDoc;
    }

    public String getReturnDoc() {
        return returnDoc;
    }

    public void setReturnDoc(String returnDoc) {
        this.returnDoc = returnDoc;
    }
}

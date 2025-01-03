package org.jrl.doc.gradle.model.doc;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Map;

/**
* @Description: //TODO (用一句话描述该文件做什么)
* @author JerryLong
*/
public class JrlProjectDoc {
    /**
     * key: 类名全路径
     * value: 类信息
     */
    private Map<String, JrlClassDoc> classDocMap;

    @JsonIgnore
    public boolean isEmpty() {
        return classDocMap == null || classDocMap.isEmpty();
    }

    public Map<String, JrlClassDoc> getClassDocMap() {
        return classDocMap;
    }

    public void setClassDocMap(Map<String, JrlClassDoc> classDocMap) {
        this.classDocMap = classDocMap;
    }
}

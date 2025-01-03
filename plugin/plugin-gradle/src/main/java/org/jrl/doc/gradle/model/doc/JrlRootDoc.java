package org.jrl.doc.gradle.model.doc;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Map;

/**
* @Description: //TODO (用一句话描述该文件做什么)
* @author JerryLong
*/
public class JrlRootDoc {
    /**
     * 项目名称
     */
    private String appName;
    /**
     * pom版本
     */
    private String pomVersion;
    /**
     * key: 子项目名称
     * value: 文档信息
     */
    private Map<String, JrlProjectDoc> projectDocMap;

    @JsonIgnore
    public boolean isEmpty() {
        return projectDocMap == null || projectDocMap.isEmpty();
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Map<String, JrlProjectDoc> getProjectDocMap() {
        return projectDocMap;
    }

    public void setProjectDocMap(Map<String, JrlProjectDoc> projectDocMap) {
        this.projectDocMap = projectDocMap;
    }

    public String getPomVersion() {
        return pomVersion;
    }

    public void setPomVersion(String pomVersion) {
        this.pomVersion = pomVersion;
    }
}

package org.jrl.doc.gradle.model;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
* 项目api配置
* @author JerryLong
*/
public class ApiConfig {
    /**
     * 工程名称
     */
    private String appName = "jrl-api-test";
    /**
     * pom版本
     */
    private String pomVersion;
    /**
     * 接口上报 host ，需要由外部指定
     */
    private String reportAddress = "http://localhost/api/doc/save";
    /**
     * 扫描路径集合
     */
    private Set<String> classPaths = new HashSet<>();
    /**
     * 扫描包集合
     */
    private Map<String, String> scannerPackageMap;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Map<String, String> getScannerPackageMap() {
        return scannerPackageMap;
    }

    public void setScannerPackageMap(Map<String, String> scannerPackageMap) {
        this.scannerPackageMap = scannerPackageMap;
    }

    public String getReportAddress() {
        return reportAddress;
    }

    public void setReportAddress(String reportAddress) {
        this.reportAddress = reportAddress;
    }

    public String getPomVersion() {
        return pomVersion;
    }

    public void setPomVersion(String pomVersion) {
        this.pomVersion = pomVersion;
    }

    public Set<String> getClassPaths() {
        return classPaths;
    }

    public void setClassPaths(Set<String> classPaths) {
        this.classPaths = classPaths;
    }
}

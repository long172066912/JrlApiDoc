/*
 * smart-doc https://github.com/shalousun/smart-doc
 *
 * Copyright (C) 2018-2020 smart-doc
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jrl.doc.gradle.util;

import org.jrl.doc.gradle.constant.JrlDocConstants;
import org.jrl.doc.gradle.model.ApiConfig;
import org.apache.commons.lang3.StringUtils;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.*;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: GradleUtil
 * @Description: 构建ApiConfig
 * @date 2020/10/23 3:13 PM
 */
public class GradleUtil {
    private static final Logger LOGGER = Logging.getLogger(Task.class);
    /**
     * 目录分隔符
     */
    private static final String FILE_SEGMEMT = "/";

    /**
     * Build ApiConfig
     *
     * @param project Project object
     * @return com.power.doc.model.ApiConfig
     */
    public static ApiConfig buildConfig(Project project) {
        ApiConfig apiConfig = new ApiConfig();
        apiConfig.setAppName(project.getName());
        //获取POM版本
        apiConfig.setPomVersion(getPomVersion(project));
        buildSourceTreeList(apiConfig, project, "compile", 0);
        buildSourceTreeList(apiConfig, project, "implementation", 0);
        buildSourceTreeList(apiConfig, project, "api", 0);
        return apiConfig;
    }

    private static void buildSourceTreeList(ApiConfig apiConfig, Project project, String compileType, int depthSize) {
        if (depthSize > 2) {
            return;
        }
        depthSize++;

        try {
            if (null == apiConfig.getScannerPackageMap()) {
                apiConfig.setScannerPackageMap(new ConcurrentHashMap<>(16));
            }

            Configuration implementation = project.getConfigurations().getByName(compileType);
            DependencySet dependencies = implementation.getDependencies();
            for (Dependency dependency : dependencies) {
                if (dependency instanceof ProjectDependency) {
                    ProjectDependency projectDependency = (ProjectDependency) dependency;
                    Project dependencyProject = projectDependency.getDependencyProject();
                    //递归寻找
                    buildSourceTreeList(apiConfig, project, "compile", depthSize);
                    buildSourceTreeList(apiConfig, project, "implementation", depthSize);
                    buildSourceTreeList(apiConfig, project, "api", depthSize);

                    String path = dependencyProject.getRootDir() + FILE_SEGMEMT + dependencyProject.getName() + JrlDocConstants.SRC_MAIN_JAVA_PATH;
                    path = path.replaceAll("\\\\", "/");
                    path = path.replaceAll("//", "/");
                    apiConfig.getScannerPackageMap().putIfAbsent(dependencyProject.getName(), path);
                }
            }

            String path = project.getRootDir() + FILE_SEGMEMT + project.getName() + JrlDocConstants.SRC_MAIN_JAVA_PATH;
            path = path.replaceAll("\\\\", "/");
            path = path.replaceAll("//", "/");

            apiConfig.getScannerPackageMap().putIfAbsent(project.getName(), path);
        } catch (Throwable e) {
        }
    }

    private static String getPomVersion(Project project) {
        String pomVersion = null;
        try {
            pomVersion = getPomVersion(project.getBuildFile());
            //如果没找到，找root下的pom版本
            if (StringUtils.isBlank(pomVersion)) {
                pomVersion = getPomVersion(project.getRootProject().getBuildFile());
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return pomVersion;
    }

    private static String getPomVersion(File buildFile) throws Throwable {
        String pomVersion = null;
        //读取文件，获取POM版本
        try (BufferedReader reader = new BufferedReader(new FileReader(buildFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // 检查是否应用了 io.spring.dependency-management 插件
                if (line.contains("mavenBom")) {
                    pomVersion = line.substring(line.lastIndexOf(":") + 1);
                    pomVersion = pomVersion.replaceAll("'", "");
                    pomVersion = pomVersion.replaceAll("\"", "");
                    pomVersion = pomVersion.substring(0, pomVersion.length() - 1);
                    break;
                }
            }
        }
        return pomVersion;
    }
}

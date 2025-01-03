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
package org.jrl.doc.gradle.task;

import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskAction;
import org.jrl.doc.gradle.constant.JrlDocConstants;
import org.jrl.doc.gradle.model.ApiConfig;
import org.jrl.doc.gradle.util.GradleUtil;

import java.util.Map;


/**
 * gradle任务插件
 *
 * @author JerryLong
 * @version V1.0
 * @date 2024/5/20 11:26
 */
public abstract class AbstractDocBaseTask extends DefaultTask {

    public static final String ROOT = "-root";

    /**
     * 执行任务
     *
     * @param apiConfig
     */
    public abstract void executeAction(ApiConfig apiConfig);

    @TaskAction
    public void action() {
        Project project = getProject();
        if (project.getName().endsWith(ROOT)) {
            return;
        }
        ApiConfig apiConfig = GradleUtil.buildConfig(project);
        //获取应用名
        final Map<String, Object> properties = (Map<String, Object>) project.getProperties();
        apiConfig.setAppName(properties.getOrDefault(JrlDocConstants.GRADLE_PROPERTIES_APP_NAME, apiConfig.getAppName()).toString());
        final Object reportAddress = properties.get(JrlDocConstants.GRADLE_PROPERTIES_REPORT_ADDRESS);
        if (null != reportAddress && reportAddress != "") {
            apiConfig.setReportAddress(reportAddress.toString());
        }
        final Object classPaths = properties.get(JrlDocConstants.GRADLE_PROPERTIES_CLASS_PATHS);
        if (null != classPaths && classPaths != "") {
            //根据逗号分隔
            final String[] paths = classPaths.toString().split(",");
            if (paths.length > 0) {
                for (String path : paths) {
                    apiConfig.getClassPaths().add(path);
                }
            }
        }
        this.executeAction(apiConfig);
    }
}

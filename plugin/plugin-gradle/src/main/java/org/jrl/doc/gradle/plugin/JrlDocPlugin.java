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
package org.jrl.doc.gradle.plugin;

import org.jrl.doc.gradle.constant.JrlDocConstants;
import org.jrl.doc.gradle.extension.JrlDocPluginExtension;
import org.jrl.doc.gradle.task.JrlApiTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.plugins.JavaPlugin;

/**
* gradle插件
* @author JerryLong
*/
public class JrlDocPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {

        Task javaCompileTask = project.getTasks().getByName(JavaPlugin.COMPILE_JAVA_TASK_NAME);

        // create open api
        JrlApiTask openApiTask = project.getTasks().create(JrlDocConstants.JRL_API_DOC_TASK, JrlApiTask.class);
        openApiTask.setGroup(JrlDocConstants.TASK_GROUP);
        openApiTask.dependsOn(javaCompileTask);

        // extend project-model to get our settings/configuration via nice configuration
        project.getExtensions().create(JrlDocConstants.JRL_API_DOC_TASK, JrlDocPluginExtension.class);
    }

}

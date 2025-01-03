package org.jrl.doc.gradle.task;

import org.jrl.doc.gradle.model.ApiConfig;
import org.jrl.doc.gradle.util.SpringApiDocUtil;

/**
* @Title: OpenApiTask
* @Description: 玩吧接口同步
* @author JerryLong
* @date 2020/10/23 2:19 PM
* @version V1.0
*/
public class JrlApiTask extends AbstractDocBaseTask {

    @Override
    public void executeAction(ApiConfig apiConfig) {
        try {
            getLogger().quiet("jrl-api-doc-gradle executeAction begin ! appName : {} , pomVersion : {}", apiConfig.getAppName(), apiConfig.getPomVersion());
            final String resp = SpringApiDocUtil.begin(apiConfig, getLogger());
            getLogger().quiet("jrl-api-doc-gradle executeAction end ! appName : {} , pomVersion : {} ! resp : {}", apiConfig.getAppName(), apiConfig.getPomVersion(), resp);
        } catch (Exception e) {
            getLogger().quiet("jrl-api-doc-gradle executeAction error !", e);
        }
    }
}

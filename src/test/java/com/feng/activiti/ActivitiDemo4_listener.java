package com.feng.activiti;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.Deployment;
import org.junit.jupiter.api.Test;

/*
* 监听器进行  分配负责人
* */
public class ActivitiDemo4_listener {

    /*
     * 流程定义 部署
     * */
    @Test
    void testDeployment(){
        ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = defaultProcessEngine.getRepositoryService();
        Deployment deploy = repositoryService.createDeployment()
                .name("chuchaishenqing-listen")
                .addClasspathResource("bpmn/evection3-listen.bpmn")
                .addClasspathResource("bpmn/evection3-listen.png")
                .deploy();
        System.out.println("流程部署id="+deploy.getId());
        System.out.println("流程部署名字="+deploy.getName());
        /*
        *
        * */
    }

    @Test
    void startDemoListener(){
        ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = defaultProcessEngine.getRuntimeService();
        runtimeService.startProcessInstanceByKey("myProcess2");
    }
}

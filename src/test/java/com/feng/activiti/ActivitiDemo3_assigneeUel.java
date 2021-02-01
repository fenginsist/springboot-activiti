package com.feng.activiti;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

/**
 * 使用 java ee  uel  进行赋值 负责人
 */
public class ActivitiDemo3_assigneeUel {


    /*
    * 流程定义 部署
    * */
    @Test
    void testDeployment(){
//        1、创建 ProcessEngine
        ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
//        2、获得 repositoryService
        RepositoryService repositoryService = defaultProcessEngine.getRepositoryService();
//        3. 使用 service 进行流程的部署，定义一个流程的名字，把bpmn 和 png 部署到数据中
        Deployment deploy = repositoryService.createDeployment()
                .name("chuchaishenqing-uel")
                .addClasspathResource("bpmn/evection2-uel.bpmn")
                .addClasspathResource("bpmn/evection2-uel.png")
                .deploy();
        //        4. 输出部署信息 从 ACT_RE_DEPLOYMENT 表中获取
        System.out.println("流程部署id="+deploy.getId());
        System.out.println("流程部署名字="+deploy.getName());
    }

    /*
    * 启动流程实例
    * */
    @Test
    void startAssigneeUel(){
        ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = defaultProcessEngine.getRuntimeService();
//        设定负责人的值
        HashMap<String, Object> assigneeMap = new HashMap<>();
        assigneeMap.put("assignee0", "张三");
        assigneeMap.put("assignee1", "李经理");
        assigneeMap.put("assignee2", "王总经理");
        assigneeMap.put("assignee3", "赵财务");
        ProcessInstance myProcess1 = runtimeService.startProcessInstanceByKey("myProcess1", assigneeMap);
//        4. 输出内容  从表  ACT_HI_ACTINST(这个表好像不太对) 中获取
        System.out.println("流程定义ID："+myProcess1.getProcessDefinitionId());
        System.out.println("流程实例ID："+myProcess1.getId());
        System.out.println("当前活动的ID："+myProcess1.getActivityId());
        /*
        流程定义ID：myProcess1:1:17504
        流程实例ID：20001
        当前活动的ID：null
        * */
    }
}

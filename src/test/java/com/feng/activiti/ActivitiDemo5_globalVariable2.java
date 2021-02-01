package com.feng.activiti;

import com.feng.activiti.Pojo.Evection;
import javafx.beans.binding.ObjectExpression;
import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

/**
 * 任务办理时设置变量
 */
public class ActivitiDemo5_globalVariable2 {
    /*
     * 流程定义 部署
     * */
    @Test
    void testDeployment(){
        ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = defaultProcessEngine.getRepositoryService();
        Deployment deploy = repositoryService.createDeployment()
                .name("chuchaishenqing-globalVariable-complete2")
                .addClasspathResource("bpmn/evection4-global.bpmn")
                .addClasspathResource("bpmn/evection4-global.png")
                .deploy();
        System.out.println("流程部署id="+deploy.getId());
        System.out.println("流程部署名字="+deploy.getName());
        /*
        流程部署id=75001
        流程部署名字=chuchaishenqing-globalVariable-complete2
         * */
    }

    /*
    * 删除 流程定义的部署
    * */
    @Test
    void deleteDeployment(){
        ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = defaultProcessEngine.getRepositoryService();
        String deploymentId = "70001";
        repositoryService.deleteDeployment(deploymentId, true);
    }

    /*
    * 启动流程，创建流程实例 的时候设置流程变量
    * 1、设置流程变量num
    * 2、设置任务负责人
    * */
    @Test
    void testStartProcess(){
        ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = defaultProcessEngine.getRuntimeService();
//        流程变量的key
        String key= "myProcess3";
//        流程变量的 map
        HashMap<String, Object> variable = new HashMap<>();

//        设置流程变量
//        Evection evection = new Evection();
//        设置出差日期
//        evection.setNum(2d);
//        variable.put("evection", evection);
//        设定任务的负责人
        variable.put("assignee0", "李四1");
        variable.put("assignee1", "王经理1");
        variable.put("assignee2", "总经理1");
        variable.put("assignee3", "孙财务1");

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(key, variable);
        System.out.println("id:"+processInstance.getId()); // id:77501
    }


    /*
    * 完成个人任务
    * 1、先执行李四 到了王经理
    * 2、在执行王经理  因为天数小于 3天，所以直接导 孙财务
    * */
    @Test
    void completTask(){
//      流程定义的 key
        String key = "myProcess3";
//        任务负责人
//        String assignee = "李四1";

//        String assignee = "王经理1";
//        Evection evection = new Evection();
//        evection.setNum(3d);
//        HashMap<String, Object> map = new HashMap<>();
//        map.put("evection", evection);

        String assignee = "总经理1";
        ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = defaultProcessEngine.getTaskService();
        Task task = taskService.createTaskQuery()
                .processDefinitionKey(key)
                .taskAssignee(assignee)
                .singleResult();
        if (null!= task){
//            taskService.complete(task.getId(), map);
            taskService.complete(task.getId());
        }
    }

}

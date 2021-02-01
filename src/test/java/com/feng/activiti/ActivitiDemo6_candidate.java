package com.feng.activiti;

import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;

/**
 * 任务办理时设置变量
 */
public class ActivitiDemo6_candidate {
    /*
     * 流程定义 部署
     * */
    @Test
    void testDeployment(){
        ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = defaultProcessEngine.getRepositoryService();
        Deployment deploy = repositoryService.createDeployment()
                .name("chuchaishenqing-candidate")
                .addClasspathResource("bpmn/evection5-candidate.bpmn")
                .deploy();
        System.out.println("流程部署id="+deploy.getId());
        System.out.println("流程部署名字="+deploy.getName());
        /*
        流程部署id=87501
        流程部署名字=chuchaishenqing-candidate
         * */
    }


    /*
    * 启动流程，创建流程实例 的时候设置流程变量
    * */
    @Test
    void testStartProcess(){
        ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = defaultProcessEngine.getRuntimeService();
//        流程定义的key
        String key = "testCandidate";
//        启动流程
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(key);
        System.out.println("id:"+processInstance.getId()); // id:90001
    }

    /**
     * 查询组任务
     */
    @Test
    void findGroupTaskList(){
//        流程定义的key
        String key = "testCandidate";
//        任务候选人
        String candidateUser = "wangwu";

        ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = defaultProcessEngine.getTaskService();
        List<Task> list = taskService.createTaskQuery()
                .processDefinitionKey("")
                .taskCandidateUser(candidateUser)
                .list();
        for (Task task : list){
            System.out.println("===============");
            System.out.println("流程实例id="+task.getProcessDefinitionId());
            System.out.println("任务id="+task.getId());
            System.out.println("任务负责人="+task.getAssignee());
        }
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

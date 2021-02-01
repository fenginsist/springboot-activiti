package com.feng.activiti;

import org.activiti.engine.*;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;

public class ActivitiDemo2_business {

    /**
     * 启动流程实例，添加businessKey
     * 插入的表
     * ACT_HI_PROCINST
     * ACT_RU_EXECUTION
     */
    @Test
    public void addBusinessKey(){
//        1、得到ProcessEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
//        2、得到RunTimeService
        RuntimeService runtimeService = processEngine.getRuntimeService();
//        3、启动流程实例，同时还要指定业务标识businessKey，也就是出差申请单id，这里是1001
//          第一个参数：流程定义的key
//          第二个参数：businessKey，存出差申请单的id，就是1001
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("myProcess","1001");
//        4、输出processInstance相关属性
        System.out.println("业务id=="+processInstance.getBusinessKey());

    }


    /**
     * 全部流程实例挂起与激活
     * Suspended 挂起
     *
     * update
     * ACT_RU_EXECUTION  SUSPENSION_STATE_=2
     * ACT_RE_PROCDEF    SUSPENSION_STATE_=2
     * ACT_RU_TASK       SUSPENSION_STATE_=2
     * 2 挂起状态， 1激活状态
     */
    @Test
    public void SuspendAllProcessInstance(){
//        1、获取 processEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
//        2、获取 repositoryService
        RepositoryService repositoryService = processEngine.getRepositoryService();
//        3、查询流程定义，获取流程定义的查询对象
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().
                processDefinitionKey("myProcess").
                singleResult();
//        4、获得当前流程定义的实例  是否都为挂起（暂停）状态
        boolean suspended = processDefinition.isSuspended();
//        5、获得流程定义id
        String processDefinitionId = processDefinition.getId();
//        6、判断是否为暂停
        if(suspended){
//         7、如果是暂停，可以执行激活操作 ,参数1 ：流程定义id ，参数2：是否激活，参数3：激活时间
            repositoryService.activateProcessDefinitionById(processDefinitionId,
                    true,
                    null
            );
            System.out.println("流程定义："+processDefinitionId+",已激活");
        }else{
//          如果是激活状态，可以暂停，参数1 ：流程定义id ，参数2：是否暂停，参数3：暂停时间
            repositoryService.suspendProcessDefinitionById(processDefinitionId,
                    true,
                    null);
            System.out.println("流程定义："+processDefinitionId+",已挂起");
        }
    }



    /**
     * 单个流程实例挂起与激活
     * update
     * ACT_RU_EXECUTION  SUSPENSION_STATE_=2
     * ACT_RU_TASK       SUSPENSION_STATE_=2
     */
    @Test
    public void SuspendSingleProcessInstance(){
//        1、获取processEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
//        2、RuntimeService
        RuntimeService runtimeService = processEngine.getRuntimeService();
//        3、查询流程定义的对象
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId("12501")
                .singleResult();
//        4、得到当前流程定义的实例是否都为暂停状态
        boolean suspended = processInstance.isSuspended();
//        流程定义id
        String processDefinitionId = processInstance.getId();
//        5、 判断是否为暂停
        if(suspended){
//         6、如果是暂停，可以执行激活操作 ,参数：流程定义id
            runtimeService.activateProcessInstanceById(processDefinitionId);
            System.out.println("流程定义："+processDefinitionId+",已激活");
        }else{
//          7、如果是激活状态，可以暂停，参数：流程定义id
            runtimeService.suspendProcessInstanceById( processDefinitionId);
            System.out.println("流程定义："+processDefinitionId+",已挂起");
        }

    }


    /*
    * 上面挂起后，进行完成个人任务测试
    * */
    @Test
    public void completTask(){
//        1、获取引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
//        2、获取操作任务的服务 TaskService
        TaskService taskService = processEngine.getTaskService();
//        3、 完成任务,参数：流程实例id,完成zhangsan的任务
        Task task = taskService.createTaskQuery()
                .processInstanceId("12501")
//                .taskAssignee("rose")
                .singleResult();

        System.out.println("流程实例id="+task.getProcessInstanceId());
        System.out.println("任务Id="+task.getId());
        System.out.println("任务负责人="+task.getAssignee());
        System.out.println("任务名称="+task.getName());
//        4、根据任务的id 完成 任务
        taskService.complete(task.getId());  // org.activiti.engine.ActivitiException: Cannot complete a suspended task
        /*
        *  激活后，在执行
        流程实例id=12501
        任务Id=12505
        任务负责人=zhangsan
        任务名称=create_work_apply
        * */
    }
}

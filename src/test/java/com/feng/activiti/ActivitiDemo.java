package com.feng.activiti;

import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricActivityInstanceQuery;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.List;
import java.util.zip.ZipInputStream;

public class ActivitiDemo {


    /**
     * 流程定义 的部署
     * 设计到的表
     * 插入的表：
     * ACT_RE_DEPLOYMENT：re资源表，流程部署表，每部署一次会增加一条记录
     * ACT_RE_PROCDEF：re资源表，流程定义表，key为唯一标识，
     * ACT_GE_BYTEARRAY：流程资源表，两个文件（bpmn=xml,png=二进制），放到这个表中
     *
     * ACT_GE_PROPERTY：修改的数据
     *
     * 查询的表
     * ACT_RU_TIMER_JOB、
     * ACT_PROCDEF_INFO、
     */
    @Test
    public void testDeplogment(){
//        1. 创建 ProcessEngine
        ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
//        2. 获取 RepositoryService
        RepositoryService repositoryService = defaultProcessEngine.getRepositoryService();
//        3. 使用 service 进行流程的部署，定义一个流程的名字，把bpmn 和 png 部署到数据中
        Deployment deployment = repositoryService.createDeployment()
                .name("chucaishenqing")
                .addClasspathResource("bpmn/evection.bpmn")
                .addClasspathResource("bpmn/evection.png")
                .deploy();
//        4. 输出部署信息 从 ACT_RE_DEPLOYMENT 表中获取
        System.out.println("流程部署id="+deployment.getId());  // 1
        System.out.println("流程部署名字="+deployment.getName()); // chucaishenqing
    }

    /**
     * 启动流程实例
     * 设计到更新的表
     * `ACT_HI_ACTINST`         流程实例执行历史
     * `ACT_HI_IDENTITYLINK`    流程参与者的历史信息
     * `ACT_HI_PROCINST`        流程实例的历史信息
     * `ACT_HI_TASKINST`        任务的历史信息
     *
     * `ACT_RU_EXECUTION`       流程执行的信息
     * `ACT_RU_IDENTITYLINK`    流程参与者信息
     * `ACT_RU_TASK`            任务信息
     */
    @Test
    public void testStartProcess(){
//        1. 创建 ProcessEngine
        ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
//        2. 获取 RuntimeService
        RuntimeService runtimeService = defaultProcessEngine.getRuntimeService();
//        3. 根据流程定义的 id 启动流程, 参数为 ACT_RE_PROCDEF 表中的 key, 也是 bpmn 的id
        ProcessInstance instance = runtimeService.startProcessInstanceByKey("myProcess");
//        4. 输出内容  从表  ACT_HI_ACTINST(这个表好像不太对) 中获取
        System.out.println("流程定义ID："+instance.getProcessDefinitionId()); // myProcess_1:1:4
        System.out.println("流程实例ID："+instance.getId());                  // 2501
        System.out.println("当前活动的ID："+instance.getActivityId());        // null
    }

    /**
     * 查询个人待执行的任务
     * 查看个人流程
     *
     * select
     * ACT_GE_PROPERTY: 配置表
     * ACT_RU_TASK：运行任务表
     * ACT_RE_PROCDEF：流程实例表
     */
    @Test
    public void testFindPersonTaskList(){
//        1. 获取流程引擎
        ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
//        2. 获取taskService
        TaskService taskService = defaultProcessEngine.getTaskService();
//        3. 根据流程key 和 任务的负责人 查询任务
        List<Task> taskList = taskService.createTaskQuery()
                .processDefinitionKey("myProcess")
                .taskAssignee("zhangsan")
                .list();
        if (StringUtils.isEmpty(taskList)){
            System.out.println("kong");
        }
        System.out.println("====================="+taskList.size());
//        4. 输出内容    从表  ACT_RU_TASK 中获取
        for (Task task : taskList){
            System.out.println("流程实例id="+task.getProcessDefinitionId()); // myProcess_1:1:4
            System.out.println("任务id="+task.getId());                      // 2505
            System.out.println("任务负责人id="+task.getAssignee());          // zhangsan
            System.out.println("任务名称id="+task.getName());                // create_apply
        }
    }

    /**
     *
     * update ACT_GE_PROPERTY  SET REV_ = ?, VALUE_ = ? where NAME_ = ? and REV_ = ?  Parameters: 4(Integer), 7501(String), next.dbid(String), 3(Integer)
     * insert into ACT_HI_TASKINST  Parameters: 5002(String), myProcess_1:1:4(String), 2501(String), 2502(String), manager(String), null, jerry(String), null, jerry(String), 2021-01-04 14:40:48.106(Timestamp)
     * insert into ACT_HI_ACTINST   Parameters: 5001(String), myProcess_1:1:4(String), 2501(String), 2502(String), _3(String), 5002(String), null, manager(String), userTask(String), jerry(String), 2021-01-04 14:40:48.087(Timestamp), null, null, null, (String)
     * insert into ACT_HI_IDENTITYLINK  Parameters: 5003(String), participant(String), jerry(String), null, null, 2501(String)
     * insert into ACT_RU_TASK      Parameters: 5002(String), manager(String), null, jerry(String), 50(Integer), 2021-01-04 14:40:48.087(Timestamp), null, jerry(String), null, 2502(String), 2501(String), myProcess_1:1:4(String), _3(String), null, null, 1(Integer), (String), null, null
     * insert into ACT_RU_IDENTITYLINK  Parameters: 5003(String), participant(String), jerry(String), null, null, 2501(String), null
     * update ACT_HI_TASKINST set PROC_DEF_ID_ = ?, EXECUTION_ID_ = ?, NAME_ = ?, PARENT_TASK_ID_ = ?, DESCRIPTION_ = ?, OWNER_ = ?, ASSIGNEE_ = ?, CLAIM_TIME_ = ?, END_TIME_ = ?, DURATION_ = ?, DELETE_REASON_ = ?, TASK_DEF_KEY_ = ?, FORM_KEY_ = ?, PRIORITY_ = ?, DUE_DATE_ = ?, CATEGORY_ = ? where ID_ = ?
     *                              Parameters : [myProcess_1:1:4, 2502, create_apply, null, zhangsan, null, 乱码（zhangsan）, null, 2021-01-04 14:40:47.885, 13987237, null, _2, null, 50, null, null, 2505]
     * update ACT_HI_ACTINST     Parameters: 2502(String), zhangsan(String), 2021-01-04 14:40:47.982(Timestamp), 13987347(Long), null, 2504(String)
     * update ACT_RU_EXECUTION   Parameters: 2(Integer), null, myProcess_1:1:4(String), _3(String), true(Boolean), false(Boolean), false(Boolean), false(Boolean), false(Boolean), 2501(String), null, 2501(String), 1(Integer), null, false(Boolean), 0(Integer), 0(Integer), 0(Integer), 0(Integer), 0(Integer), 0(Integer), 0(Integer), 0(Integer), 2502(String), 1(Integer)
     * delete from ACT_RU_TASK where ID_ = ? and REV_ = ?    Parameters: 2505(String), 1(Integer)
     */
    @Test
    void completeTask(){
//        1. 获取引擎
        ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
//        2. 获取 taskService
        TaskService taskService = defaultProcessEngine.getTaskService();
//        根据任务id 完成任务, 完成张三的任务
//        taskService.complete("2505");
//        获取 jerry - myProcess_1 对应的任务
//        Task task = taskService.createTaskQuery()
//                .processDefinitionKey("myProcess_1")
//                .taskAssignee("jerry")
//                .singleResult();
//        完成 Jack 的任务
//        Task task = taskService.createTaskQuery()
//                .processDefinitionKey("myProcess_1")
//                .taskAssignee("Jack")
//                .singleResult();
//        完成 Jack 的任务
        Task task = taskService.createTaskQuery()
                .processDefinitionKey("myProcess")
                .taskAssignee("rose")
                .singleResult();
        System.out.println("流程实例id="+task.getProcessDefinitionId()); //
        System.out.println("任务id="+task.getId());                      //
        System.out.println("任务负责人id="+task.getAssignee());          //
        System.out.println("任务名称id="+task.getName());
        /*
        * 流程实例id=myProcess_1:1:4
          任务id=5002
          任务负责人id=jerry
          任务名称id=manager
        * */
        /*
        * 流程实例id=myProcess_1:1:4
          任务id=7502
          任务负责人id=Jack
          任务名称id=all_manager
        * */
        /*
        * 流程实例id=myProcess_1:1:4
        任务id=10002
        任务负责人id=rose
        任务名称id=cash_manager
        * */
        String id = task.getId();
        taskService.complete(id);
    }


    /**
     * 使用 zip 方式 进行批量的部署
     */
    @Test
    void deployProcessByZip(){
        // 定义zip输入流
        InputStream inputStream = this
                .getClass()
                .getClassLoader()
                .getResourceAsStream(
                        "bpmn/evection.zip");
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
        // 获取repositoryService
        RepositoryService repositoryService = defaultProcessEngine.getRepositoryService();
        // 流程部署
        Deployment deployment = repositoryService.createDeployment()
                .addZipInputStream(zipInputStream)
                .deploy();
        System.out.println("流程部署id：" + deployment.getId()); // 1
        System.out.println("流程部署名称：" + deployment.getName()); // null ，因为没有写名字
    }

    /**
     *  查询流程相关信息，包含流程定义，流程部署，流程定义版本
     *  从 ACT_RE_PROCDEF 表中 获取
     */
    @Test
    public void queryProcessDefinition(){
//        获取引擎
        ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
//        获取 RepositoryService
        RepositoryService repositoryService = defaultProcessEngine.getRepositoryService();
//        得到ProcessDefinitionQuery 对象
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
//          查询出当前流程 所有的定义
//          条件：processDefinitionKey =evection
//          orderByProcessDefinitionVersion 按照版本排序
//        desc倒叙
//        list 返回集合
        List<ProcessDefinition> myProcess = processDefinitionQuery.processDefinitionKey("myProcess")
                .orderByProcessDefinitionVersion()
                .desc()
                .list();
        for (ProcessDefinition processDefinition : myProcess){
            System.out.println("流程定义 id="+processDefinition.getId());
            System.out.println("流程定义 name="+processDefinition.getName());
            System.out.println("流程定义 key="+processDefinition.getKey());
            System.out.println("流程定义 Version="+processDefinition.getVersion());
            System.out.println("流程部署ID ="+processDefinition.getDeploymentId());
        }
        /*
        流程定义 id=myProcess:1:4
        流程定义 name=null
        流程定义 key=myProcess
        流程定义 Version=1
        流程部署ID =1
        * */
    }

    /**
     *  流程定义 删除
     *  ACT_RE_PROCDEF、
     *  ACT_RE_DEPLOYMENT、
     *  ACT_GE_BYTEARRAY 中数据 都删啦
     *  当前 的流程如果没有完成，想要删除的话需要使用特殊方式，原理就是 级联删除
     *  这样也会删除  ACT_RU_TASK 表
     */
    @Test
    public void deleteDeployment() {
        // 流程部署id
        String deploymentId = "2501";

        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 通过流程引擎获取repositoryService
        RepositoryService repositoryService = processEngine.getRepositoryService();
        //删除流程定义，如果该流程定义已有流程实例启动则删除时出错
//        repositoryService.deleteDeployment(deploymentId);
        //设置true 级联删除流程定义，即使该流程有流程实例启动也可以删除，设置为false非级别删除方式，如果流程
        repositoryService.deleteDeployment(deploymentId, true);
    }

    /**
     * 删除流程实例
     * update
     * ACT_HI_PROCINST
     * ACT_RU_EXECUTION
     * ACT_RU_EXECUTION
     * ACT_HI_TASKINST
     * ACT_HI_ACTINST
     *
     * delete
     * ACT_RU_TASK
     * ACT_RU_EXECUTION
     */
    @Test
    void deleteRunTask(){
        ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = defaultProcessEngine.getRuntimeService();
//        第一个参数：ACT_RUN_TASK 中的 PROC_INST_ID_ 字段
//        第二个参数：流程定义 id
        runtimeService.deleteProcessInstance("42501", "myProcess2");
    }


    /*
    * 下载 资源文件
    * 方案1：使用 activiti 提供的 api，来下载资源文件
    * 方案2：自己写代码从数据库中下载文件，使用 jdbc 对 blob 类型，clob类型数据读取出来，保存到奥文件目录
    * 解决IO操作： commons-io.jar
    * 这里我们使用方案1：RepositoryService
    * */
    @Test
    void getDeployment() throws IOException {
//      1、得到引擎
        ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
//        2、获取api
        RepositoryService repositoryService = defaultProcessEngine.getRepositoryService();
//        3、获取查询对象   查询流程定义信息
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey("myProcess")
                .singleResult();
//        4、通过 流程定义信息，获取部署ID
        String deploymentId = processDefinition.getDeploymentId();
//        5、通过  ，传递部署id参数，读取资源信息（png,bpmn）
//        5.1 获取png 图片的流
//              从流程定义表中，获取png图片的目录和名字
        String pngName = processDefinition.getDiagramResourceName();
        InputStream pngStream = repositoryService.getResourceAsStream(deploymentId, pngName);
//        5.2 获取bpmn的流
        String bpmnName = processDefinition.getResourceName();
        InputStream bpmnStream = repositoryService.getResourceAsStream(deploymentId, bpmnName);
//        6、构造 OutPutStream 流
        File pngFile = new File("d:/evectionflow01.png");
        File bpmnFile = new File("d:/evectionflow01.bpmn");
        FileOutputStream pngOutputStream = new FileOutputStream(pngFile);
        FileOutputStream bpmnOutputStream = new FileOutputStream(bpmnFile);

//        7、输入流、输出流的转换, 注意包 IOUtils 为 commons-io.jar中的
        IOUtils.copy(pngStream, pngOutputStream);
        IOUtils.copy(bpmnStream, bpmnOutputStream);
//        8、关闭流
        pngStream.close();
        bpmnStream.close();
    }

    /**
     * 查看历史信息
     * 查询 actinst 表
     */
    @Test
    void findHistoryInfo(){
//        1、获取引擎
        ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
//        2、获取 HistoryService
        HistoryService historyService = defaultProcessEngine.getHistoryService();
//        3、获取 actinst 表的查询对象
        HistoricActivityInstanceQuery historicActivityInstanceQuery = historyService.createHistoricActivityInstanceQuery();
//        4、查询 actinst 表 PROC_INST_ID_ 和 PROC_DEF_ID_ 字段, 只有以下两个方法查询，也就是只能根据这两个字段查询
//        historicActivityInstanceQuery.processInstanceId("10001");
//        historicActivityInstanceQuery.processDefinitionId("myProcess:1:7504");
//        增加排序操作  orderByHistoricActivityInstanceStartTime 根据开始时间排序 asc 升序
        historicActivityInstanceQuery.orderByHistoricActivityInstanceStartTime().asc();
//        5、查询所有内容
        List<HistoricActivityInstance> list = historicActivityInstanceQuery.list();
//        6、输出
        for (HistoricActivityInstance hi : list) {
            System.out.println(hi.getActivityId());
            System.out.println(hi.getActivityName());
            System.out.println(hi.getProcessDefinitionId());
            System.out.println(hi.getProcessInstanceId());
            System.out.println("<==========================>");
        }

    }
}

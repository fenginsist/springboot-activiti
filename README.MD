# 一、activiti 基础

## 1、Activiti的表说明
    使用25张表
    ACT_RE  流程定义和流程资源
    ACT_RU  运行时，流程实例、任务、变量
    ACT_HI  历史表
    ACT_GE  通用表

## 2、Activiti的架构、类关系图
    获取流程引擎的工具类
    ProcessEngines： 使用默认方式获取配置文件，构造流程引擎，配置文件名字 activiti.cfg.xml，放在 classpath 下
    ProcessEngineConfiguration：可以自定义配置文件名
    使用上面2个工具类，都可以获得流程引擎
    ProcessEngine： 流程引擎。获取各种服务的接口。
    服务接口：用于流程的部署、执行、管理，使用这些接口就是在操作对应的数据表
        RepositoryService   资源管理类
        RuntimeService      运行时管理类
        TaskService         任务管理类
        HistoryService      历史数据管理类
        ManagementService   流程引擎管理类

## 3、BPM插件
    idea 安装 actiBPM 插件
    
## 4、流程符号、画流程图
    流程符号： 事件event、活动Activity、网关Gateway，流向
    使用流程设计器画出流程图
    bpmn文件本质上是xml 文件，因为安装 actiBPM插件，才能可视化的看到流程内容
    创建bpmn文件，在流程设计器符号来表达流程。指定流程的 key, 指定任务负责人
    生成 PNG 文件，把 bpmn文件后缀改为 xml, 在这个文件上右键选择 Diagrams-》show NPMN2.0 Desinger, 打开窗口，点击导出文件
    选择导出文件类型为PNG，要注意乱码问题。

## 5、部署流程
    使用 Activiti提供的API把流程图的内容写入数据库中
    属于资源类操作，使用 RepositoryService
    单文件部署：把bpmn文件和png文件一个一个处理
    压缩包部署：把bpmn文件和png打压缩包来处理
    Deployment deployment = repositoryService.createDeployment()
    部署操作表：
     * ACT_RE_DEPLOYMENT：re资源表，流程部署表，每部署一次会增加一条记录
     * ACT_RE_PROCDEF：re资源表，流程定义表，key为唯一标识，
     * ACT_GE_BYTEARRAY：流程资源表，两个文件（bpmn=xml,png=二进制），放到这个表中
     * ACT_GE_PROPERTY：修改的数据

## 6、启动流程实例
    使用 RuntimeService 根据 流程定义的key
    核心代码：
    ProcessInstance instance = runtimeService.startProcessInstanceByKey("myProcess_1");  
    操作表：
     * `ACT_HI_ACTINST`         流程实例执行历史
     * `ACT_HI_IDENTITYLINK`    流程参与者的历史信息
     * `ACT_HI_PROCINST`        流程实例的历史信息
     * `ACT_HI_TASKINST`        任务的历史信息
     * `ACT_RU_EXECUTION`       流程执行的信息
     * `ACT_RU_IDENTITYLINK`    流程参与者信息
     * `ACT_RU_TASK`            任务信息
## 7、任务查询
    使用 TaskService ，根据流程定义的key,任务的负责人来进行查询
    核心代码
    ```
    List<Task> taskList = taskService.createTaskQuery()
            .processDefinitionKey("myProcess_1")
            .taskAssignee("zhangsan")
            .list();
    ```
## 8、任务完成
    使用 TaskService， 用任务 ID 完成任务
    核心代码
    ```
    taskService.complete("2501")
    ```

## 9. 流程定义
    根据流程定义的 key, 查询流程定义的信息

## 10. 流程删除
    使用 deploymentid 删除流程

## 11. 流程资源下载
    如果其他用户想要查看流程的资源文件，可以从数据库中把资源文件下载到本地

## 12. 流程历史信息查询
    HistoryService

# 二、activiti 进阶

## 1. 流程定义和流程实例
    一个流程定义可以对应多个流程实例
    如：公司有 出差申请流程  流程定义
         职员   小张申请出差  广州出差4天  流程实例
                 小李申请出差 杭州出差5天  流程实例
    
## 2. 业务标识 BusinessKey
    可以查询自己的业务 信息
    如：小张出差 目的地，出差时长
    使用 RuntimeService
    ```
    ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("myProcess","1001");
    ```
## 3. 挂起、激活流程实例
    单个挂起：使用RuntimeService操作流程实例。某个流程实例挂起则此流程不再继续执行，如果想完成该实例，会报错
    全部挂起：使用RepositoryService操作流程定义。流程定义为挂起状态，则不允许添加新的流程的实例，同时，该流程定义下的所有流程实例
              全部执行挂起操作
               使用流程定义的id来执行挂起或激活操作




    
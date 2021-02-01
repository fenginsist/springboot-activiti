package com.feng.activiti.listener;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

/*
* 监听器
* */
public class MyTaskListener implements TaskListener {

    /**
     * 指定负责人
     * @param delegateTask
     */
    @Override
    public void notify(DelegateTask delegateTask) {
        if ("create_apply".equals(delegateTask.getName()) &&
                "create".equals(delegateTask.getEventName())){   // create  为固定
            delegateTask.setAssignee("zhangsan");
        }
    }
}

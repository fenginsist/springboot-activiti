package com.feng.activiti.Pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/*
* 出差申请中的流程变量对象
* evection4-global.bpmn
* */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Evection implements Serializable {

    /*
    * 主键 id
    * */
    private String id;

    /*
    *出差单名字
    * */
    private String evectionName;

    /*
    * 出差天数
    * */
    private Double num;

    /*
    * 出差开始时间
    * */
    private Date beginDate;

    /*
    * 出差结束时间
    * */
    private Date endDate;

    /*
    * 目的地
    * */
    private String destination;

    /*
    * 出差原因
    * */
    private String reason;
}

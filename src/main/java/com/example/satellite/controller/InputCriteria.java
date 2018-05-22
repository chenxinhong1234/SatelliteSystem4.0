package com.example.satellite.controller;

import java.util.Date;

/**
 * Description: 厂内经济运行的输入条件
 * Created by Gaoxinwen on 2017/3/23.
 */
public class InputCriteria {

    // 调度日期
    private Date date;
    // 初始水位
    private double initialLevel;
    // 出力
    private double[] output;
    // 开停机计划
    private int[] schedule;
    // 入库
    private double[] inflow;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getInitialLevel() {
        return initialLevel;
    }

    public void setInitialLevel(double initialLevel) {
        this.initialLevel = initialLevel;
    }

    public double[] getOutput() {
        return output;
    }

    public void setOutput(double[] output) {
        this.output = output;
    }

    public int[] getSchedule() {
        return schedule;
    }

    public void setSchedule(int[] schedule) {
        this.schedule = schedule;
    }

    public double[] getInflow() {
        return inflow;
    }

    public void setInflow(double[] inflow) {
        this.inflow = inflow;
    }
}

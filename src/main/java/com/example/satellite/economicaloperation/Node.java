package com.example.satellite.economicaloperation;

/**
 * Description: 机组组合的节点类
 * Created by Gaoxinwen on 2016/9/12.
 */
public class Node {
    // 编号
    private int index;
    // 机组组合
    private int[] uc;
    // 机组出力
    private double[] N;
    // 机组耗流
    private double[] Q;
    // 是否可行
    private boolean enabled;
    // 前一个节点
    private Node previous;
    // 总目标值
    private double fitness;

    // 时段末水位
    private double finalLevel;
    // 水头
    private double head;
    // 弃水
    private double aflow;


    /*
     * 开、关机持续时间：正表示开机持续时间；负表示关机持续时间；不可能为0
     */
    private int[] duration;


    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int[] getUc() {
        return uc;
    }

    public void setUc(int[] uc) {
        this.uc = uc;
    }

    public double[] getN() {
        return N;
    }

    public void setN(double[] n) {
        N = n;
    }

    public double[] getQ() {
        return Q;
    }

    public void setQ(double[] q) {
        Q = q;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Node getPrevious() {
        return previous;
    }

    public void setPrevious(Node previous) {
        this.previous = previous;
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public double getFinalLevel() {
        return finalLevel;
    }

    public void setFinalLevel(double finalLevel) {
        this.finalLevel = finalLevel;
    }

    public double getHead() {
        return head;
    }

    public void setHead(double head) {
        this.head = head;
    }

    public int[] getDuration() {
        return duration;
    }

    public void setDuration(int[] duration) {
        this.duration = duration;
    }

    public double getAflow() {
        return aflow;
    }

    public void setAflow(double aflow) {
        this.aflow = aflow;
    }
}

package com.example.satellite.util;

import java.util.List;

/**
 * Description:
 * Created by Gaoxinwen on 2016/9/9.
 */
public class IntervalCustom {
    // 振动区
    private List<Interval> intervals;
    // 总出力区
    private Interval domain;

    public IntervalCustom(List<Interval> intervals, Interval domain) {
        this.intervals = intervals;
        this.domain = domain;
    }

    public List<Interval> getIntervals() {
        return intervals;
    }

    public void setIntervals(List<Interval> intervals) {
        this.intervals = intervals;
    }

    public Interval getDomain() {
        return domain;
    }

    public void setDomain(Interval domain) {
        this.domain = domain;
    }

    @Override
    public String toString() {
        return "IntervalCustom{" +
                "intervals=" + intervals +
                ", domain=" + domain +
                '}';
    }
}

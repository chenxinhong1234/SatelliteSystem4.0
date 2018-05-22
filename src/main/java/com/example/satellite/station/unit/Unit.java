package com.example.satellite.station.unit;

import com.example.satellite.station.unit.curve.HeadExceptedOutputCurve;
import com.example.satellite.station.unit.curve.NHQCurve;
import com.example.satellite.util.Interval;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:机组类
 * Created by Gaoxinwen on 2016/4/15.
 */
public class Unit {
    // 机组编号
    private Integer unitId;
    // 机组类型
    private Integer groupId;
    // 电站编号
    private Integer stationId;
    // NHQ曲线
    private NHQCurve nhqCurve;
    // 预想出力曲线
    private HeadExceptedOutputCurve headExceptedOutputCurve;
    // 振动区
    private List<Interval> pozList = new ArrayList<>();

    public Unit(Integer unitId, Integer groupId, Integer stationId, NHQCurve nhqCurve, HeadExceptedOutputCurve
            headExceptedOutputCurve, List<Interval> pozList) {
        this.unitId = unitId;
        this.groupId = groupId;
        this.stationId = stationId;
        this.nhqCurve = nhqCurve;
        this.headExceptedOutputCurve = headExceptedOutputCurve;
        this.pozList = pozList;
    }

    public Integer getUnitId() {
        return unitId;
    }

    public void setUnitId(Integer unitId) {
        this.unitId = unitId;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public Integer getStationId() {
        return stationId;
    }

    public void setStationId(Integer stationId) {
        this.stationId = stationId;
    }

    public NHQCurve getNhqCurve() {
        return nhqCurve;
    }

    public void setNhqCurve(NHQCurve nhqCurve) {
        this.nhqCurve = nhqCurve;
    }

    public HeadExceptedOutputCurve getHeadExceptedOutputCurve() {
        return headExceptedOutputCurve;
    }

    public void setHeadExceptedOutputCurve(HeadExceptedOutputCurve headExceptedOutputCurve) {
        this.headExceptedOutputCurve = headExceptedOutputCurve;
    }

    public List<Interval> getPozList() {
        return pozList;
    }

    public void setPozList(List<Interval> pozList) {
        this.pozList = pozList;
    }
}

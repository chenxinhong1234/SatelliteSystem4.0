package com.example.satellite.domain;

import javax.persistence.*;

/**
 * Description: 每一个group包含不止一个unit
 * Created by Gaoxinwen on 2016/5/3.
 */
@Entity
@Table(name = "unitgroup", schema = "reservoirdispatchsystem")
public class UnitgroupEntity {
    private int id;
    private int stationId;
    private int groupId;
    private int unitId;

    @Id
    @Column(name = "id", nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "station_id", nullable = false)
    public int getStationId() {
        return stationId;
    }

    public void setStationId(int stationId) {
        this.stationId = stationId;
    }

    @Basic
    @Column(name = "group_id", nullable = false)
    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    @Basic
    @Column(name = "unit_id", nullable = false)
    public int getUnitId() {
        return unitId;
    }

    public void setUnitId(int unitId) {
        this.unitId = unitId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UnitgroupEntity that = (UnitgroupEntity) o;

        if (id != that.id) return false;
        if (stationId != that.stationId) return false;
        if (groupId != that.groupId) return false;
        if (unitId != that.unitId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + stationId;
        result = 31 * result + groupId;
        result = 31 * result + unitId;
        return result;
    }
}

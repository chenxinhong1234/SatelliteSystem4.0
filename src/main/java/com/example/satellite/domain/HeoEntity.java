package com.example.satellite.domain;

import javax.persistence.*;

/**
 * Description:
 * Created by Gaoxinwen on 2016/5/3.
 */
@Entity
@Table(name = "heo", schema = "reservoirdispatchsystem")
public class HeoEntity {
    private int id;
    private int stationId;
    private int groupId;
    private double h;
    private double eOutput;

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
    @Column(name = "h", nullable = false, precision = 0)
    public double getH() {
        return h;
    }

    public void setH(double h) {
        this.h = h;
    }

    @Basic
    @Column(name = "e_output", nullable = false, precision = 0)
    public double geteOutput() {
        return eOutput;
    }

    public void seteOutput(double eOutput) {
        this.eOutput = eOutput;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HeoEntity heoEntity = (HeoEntity) o;

        if (id != heoEntity.id) return false;
        if (stationId != heoEntity.stationId) return false;
        if (groupId != heoEntity.groupId) return false;
        if (Double.compare(heoEntity.h, h) != 0) return false;
        if (Double.compare(heoEntity.eOutput, eOutput) != 0) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id;
        result = 31 * result + stationId;
        result = 31 * result + groupId;
        temp = Double.doubleToLongBits(h);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(eOutput);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}

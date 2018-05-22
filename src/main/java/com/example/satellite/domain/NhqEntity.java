package com.example.satellite.domain;

import javax.persistence.*;

/**
 * Description:
 * Created by Gaoxinwen on 2016/5/3.
 */
@Entity
@Table(name = "nhq", schema = "reservoirdispatchsystem")
public class NhqEntity {
    private int id;
    private int stationId;
    private int groupId;
    private double h;
    private double n;
    private double q;

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
    @Column(name = "n", nullable = false, precision = 0)
    public double getN() {
        return n;
    }

    public void setN(double n) {
        this.n = n;
    }

    @Basic
    @Column(name = "q", nullable = false, precision = 0)
    public double getQ() {
        return q;
    }

    public void setQ(double q) {
        this.q = q;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NhqEntity nhqEntity = (NhqEntity) o;

        if (id != nhqEntity.id) return false;
        if (stationId != nhqEntity.stationId) return false;
        if (groupId != nhqEntity.groupId) return false;
        if (Double.compare(nhqEntity.h, h) != 0) return false;
        if (Double.compare(nhqEntity.n, n) != 0) return false;
        if (Double.compare(nhqEntity.q, q) != 0) return false;

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
        temp = Double.doubleToLongBits(n);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(q);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}

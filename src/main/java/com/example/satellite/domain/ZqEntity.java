package com.example.satellite.domain;

import javax.persistence.*;

/**
 * Description:
 * Created by Gaoxinwen on 2016/5/3.
 */
@Entity
@Table(name = "zq", schema = "reservoirdispatchsystem")
public class ZqEntity {
    private int id;
    private int stationId;
    private double z;
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
    @Column(name = "z", nullable = false, precision = 0)
    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
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

        ZqEntity zqEntity = (ZqEntity) o;

        if (id != zqEntity.id) return false;
        if (stationId != zqEntity.stationId) return false;
        if (Double.compare(zqEntity.z, z) != 0) return false;
        if (Double.compare(zqEntity.q, q) != 0) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id;
        result = 31 * result + stationId;
        temp = Double.doubleToLongBits(z);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(q);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}

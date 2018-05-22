package com.example.satellite.domain;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Description:
 * Created by Gaoxinwen on 2016/9/8.
 */
@Entity
public class Poz {
    private int id;
    private int stationId;
    private int unitId;
    private double lower;
    private double upper;

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "station_id")
    public int getStationId() {
        return stationId;
    }

    public void setStationId(int stationId) {
        this.stationId = stationId;
    }

    @Basic
    @Column(name = "unit_id")
    public int getUnitId() {
        return unitId;
    }

    public void setUnitId(int unitId) {
        this.unitId = unitId;
    }

    @Basic
    @Column(name = "lower")
    public double getLower() {
        return lower;
    }

    public void setLower(double lower) {
        this.lower = lower;
    }

    @Basic
    @Column(name = "upper")
    public double getUpper() {
        return upper;
    }

    public void setUpper(double upper) {
        this.upper = upper;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Poz poz = (Poz) o;

        if (id != poz.id) return false;
        if (stationId != poz.stationId) return false;
        if (unitId != poz.unitId) return false;
        if (Double.compare(poz.lower, lower) != 0) return false;
        if (Double.compare(poz.upper, upper) != 0) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id;
        result = 31 * result + stationId;
        result = 31 * result + unitId;
        temp = Double.doubleToLongBits(lower);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(upper);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}

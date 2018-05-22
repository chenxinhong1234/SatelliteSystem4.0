package com.example.satellite.domain;

import javax.persistence.*;

/**
 * Description:
 * Created by Gaoxinwen on 2016/5/3.
 */
@Entity               /*对实体注释。任何Hibernate映射对象都要有这个注释*/
@Table(name = "zv", schema = "reservoirdispatchsystem")
/*确定了连接哪个数据库（reservoirdispatchsystem），和数据库中的哪个表格（zv），
        这些是自动生成的，在屏幕左下角的persistence里面*/
public class ZvEntity {
    private int id;
    private int stationId;
    private double z;
    private double v;

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
    @Column(name = "v", nullable = false, precision = 0)
    public double getV() {
        return v;
    }

    public void setV(double v) {
        this.v = v;
    }

    /*没看懂，但对结果没啥影响*/
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ZvEntity zvEntity = (ZvEntity) o;

        if (id != zvEntity.id) return false;
        if (stationId != zvEntity.stationId) return false;
        if (Double.compare(zvEntity.z, z) != 0) return false;
        if (Double.compare(zvEntity.v, v) != 0) return false;

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
        temp = Double.doubleToLongBits(v);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}

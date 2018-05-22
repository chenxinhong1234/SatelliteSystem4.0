package com.example.satellite.domain;

import javax.persistence.*;

/**
 * Description:
 * Created by Gaoxinwen on 2016/5/3.
 */
@Entity
@Table(name = "stationinfo", schema = "reservoirdispatchsystem")
public class StationinfoEntity {
    private int id;
    private String name;
    private String riverbasin;
    private Integer regulationperformance;
    private Double normallevel;
    private Double floodcontrollevel;
    private Double deadlevel;
    private Double installedcapacity;
    private Double guaranteedoutput;

    @Id
    @Column(name = "id", nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "name", nullable = false, length = 255)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "riverbasin", nullable = true, length = 255)
    public String getRiverbasin() {
        return riverbasin;
    }

    public void setRiverbasin(String riverbasin) {
        this.riverbasin = riverbasin;
    }

    @Basic
    @Column(name = " regulationperformance", nullable = true)
    public Integer getRegulationperformance() {
        return regulationperformance;
    }

    public void setRegulationperformance(Integer regulationperformance) {
        this.regulationperformance = regulationperformance;
    }

    @Basic
    @Column(name = "normallevel", nullable = true, precision = 0)
    public Double getNormallevel() {
        return normallevel;
    }

    public void setNormallevel(Double normallevel) {
        this.normallevel = normallevel;
    }

    @Basic
    @Column(name = "floodcontrollevel", nullable = true, precision = 0)
    public Double getFloodcontrollevel() {
        return floodcontrollevel;
    }

    public void setFloodcontrollevel(Double floodcontrollevel) {
        this.floodcontrollevel = floodcontrollevel;
    }

    @Basic
    @Column(name = "deadlevel", nullable = true, precision = 0)
    public Double getDeadlevel() {
        return deadlevel;
    }

    public void setDeadlevel(Double deadlevel) {
        this.deadlevel = deadlevel;
    }

    @Basic
    @Column(name = "installedcapacity", nullable = true, precision = 0)
    public Double getInstalledcapacity() {
        return installedcapacity;
    }

    public void setInstalledcapacity(Double installedcapacity) {
        this.installedcapacity = installedcapacity;
    }

    @Basic
    @Column(name = " guaranteedoutput", nullable = true, precision = 0)
    public Double getGuaranteedoutput() {
        return guaranteedoutput;
    }

    public void setGuaranteedoutput(Double guaranteedoutput) {
        this.guaranteedoutput = guaranteedoutput;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StationinfoEntity that = (StationinfoEntity) o;

        if (id != that.id) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (riverbasin != null ? !riverbasin.equals(that.riverbasin) : that.riverbasin != null) return false;
        if (regulationperformance != null ? !regulationperformance.equals(that.regulationperformance) : that.regulationperformance != null)
            return false;
        if (normallevel != null ? !normallevel.equals(that.normallevel) : that.normallevel != null) return false;
        if (floodcontrollevel != null ? !floodcontrollevel.equals(that.floodcontrollevel) : that.floodcontrollevel != null)
            return false;
        if (deadlevel != null ? !deadlevel.equals(that.deadlevel) : that.deadlevel != null) return false;
        if (installedcapacity != null ? !installedcapacity.equals(that.installedcapacity) : that.installedcapacity != null)
            return false;
        if (guaranteedoutput != null ? !guaranteedoutput.equals(that.guaranteedoutput) : that.guaranteedoutput != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (riverbasin != null ? riverbasin.hashCode() : 0);
        result = 31 * result + (regulationperformance != null ? regulationperformance.hashCode() : 0);
        result = 31 * result + (normallevel != null ? normallevel.hashCode() : 0);
        result = 31 * result + (floodcontrollevel != null ? floodcontrollevel.hashCode() : 0);
        result = 31 * result + (deadlevel != null ? deadlevel.hashCode() : 0);
        result = 31 * result + (installedcapacity != null ? installedcapacity.hashCode() : 0);
        result = 31 * result + (guaranteedoutput != null ? guaranteedoutput.hashCode() : 0);
        return result;
    }
}

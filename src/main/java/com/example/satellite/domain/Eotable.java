package com.example.satellite.domain;

import com.example.satellite.util.excel.ExcelField;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Description:
 * Created by Gaoxinwen on 2016/6/18.
 */
@Entity
public class Eotable implements Cloneable{
    @ExcelField
    private int id;
    @ExcelField
    private double head;
    @ExcelField
    private double sumN;
    @ExcelField
    private double n1;
    @ExcelField
    private double n2;
    @ExcelField
    private double n3;
    @ExcelField
    private double n4;
    @ExcelField
    private double n5;
    @ExcelField
    private double n6;
    @ExcelField
    private double sumQ;
    @ExcelField
    private double q1;
    @ExcelField
    private double q2;
    @ExcelField
    private double q3;
    @ExcelField
    private double q4;
    @ExcelField
    private double q5;
    @ExcelField
    private double q6;

    private int[] uc;
    public int[] findUC() {
        if (uc == null) {
            uc = new int[6];
            for (int i = 0; i < uc.length; i++) {
                uc[i] = (findNArray()[i] == 0) ? 0 : 1;
            }
        }
        return uc;
    }

    public void setN(double[] n) {
        setN1(n[0]);
        setN2(n[1]);
        setN3(n[2]);
        setN4(n[3]);
        setN5(n[4]);
        setN6(n[5]);
    }

    public void setQ(double[] q) {
        setQ1(q[0]);
        setQ2(q[1]);
        setQ3(q[2]);
        setQ4(q[3]);
        setQ5(q[4]);
        setQ6(q[5]);
    }

    public double[] findQArray() {
        return new double[]{q1, q2, q3, q4, q5, q6};
    }

    public double[] findNArray() {
        return new double[]{n1, n2, n3, n4, n5, n6};
    }

    @Id
    @Column(name = "id", nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "head", nullable = false, precision = 0)
    public double getHead() {
        return head;
    }

    public void setHead(double head) {
        this.head = head;
    }

    @Basic
    @Column(name = "sumN", nullable = false, precision = 0)
    public double getSumN() {
        return sumN;
    }

    public void setSumN(double sumN) {
        this.sumN = sumN;
    }

    @Basic
    @Column(name = "N1", nullable = false, precision = 0)
    public double getN1() {
        return n1;
    }

    public void setN1(double n1) {
        this.n1 = n1;
    }

    @Basic
    @Column(name = "N2", nullable = false, precision = 0)
    public double getN2() {
        return n2;
    }

    public void setN2(double n2) {
        this.n2 = n2;
    }

    @Basic
    @Column(name = "N3", nullable = false, precision = 0)
    public double getN3() {
        return n3;
    }

    public void setN3(double n3) {
        this.n3 = n3;
    }

    @Basic
    @Column(name = "N4", nullable = false, precision = 0)
    public double getN4() {
        return n4;
    }

    public void setN4(double n4) {
        this.n4 = n4;
    }

    @Basic
    @Column(name = "N5", nullable = false, precision = 0)
    public double getN5() {
        return n5;
    }

    public void setN5(double n5) {
        this.n5 = n5;
    }

    @Basic
    @Column(name = "N6", nullable = false, precision = 0)
    public double getN6() {
        return n6;
    }

    public void setN6(double n6) {
        this.n6 = n6;
    }

    @Basic
    @Column(name = "sumQ", nullable = false, precision = 0)
    public double getSumQ() {
        return sumQ;
    }

    public void setSumQ(double sumQ) {
        this.sumQ = sumQ;
    }

    @Basic
    @Column(name = "Q1", nullable = false, precision = 0)
    public double getQ1() {
        return q1;
    }

    public void setQ1(double q1) {
        this.q1 = q1;
    }

    @Basic
    @Column(name = "Q2", nullable = false, precision = 0)
    public double getQ2() {
        return q2;
    }

    public void setQ2(double q2) {
        this.q2 = q2;
    }

    @Basic
    @Column(name = "Q3", nullable = false, precision = 0)
    public double getQ3() {
        return q3;
    }

    public void setQ3(double q3) {
        this.q3 = q3;
    }

    @Basic
    @Column(name = "Q4", nullable = false, precision = 0)
    public double getQ4() {
        return q4;
    }

    public void setQ4(double q4) {
        this.q4 = q4;
    }

    @Basic
    @Column(name = "Q5", nullable = false, precision = 0)
    public double getQ5() {
        return q5;
    }

    public void setQ5(double q5) {
        this.q5 = q5;
    }

    @Basic
    @Column(name = "Q6", nullable = false, precision = 0)
    public double getQ6() {
        return q6;
    }

    public void setQ6(double q6) {
        this.q6 = q6;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Eotable eotable = (Eotable) o;

        if (id != eotable.id) return false;
        if (Double.compare(eotable.head, head) != 0) return false;
        if (Double.compare(eotable.sumN, sumN) != 0) return false;
        if (Double.compare(eotable.n1, n1) != 0) return false;
        if (Double.compare(eotable.n2, n2) != 0) return false;
        if (Double.compare(eotable.n3, n3) != 0) return false;
        if (Double.compare(eotable.n4, n4) != 0) return false;
        if (Double.compare(eotable.n5, n5) != 0) return false;
        if (Double.compare(eotable.n6, n6) != 0) return false;
        if (Double.compare(eotable.sumQ, sumQ) != 0) return false;
        if (Double.compare(eotable.q1, q1) != 0) return false;
        if (Double.compare(eotable.q2, q2) != 0) return false;
        if (Double.compare(eotable.q3, q3) != 0) return false;
        if (Double.compare(eotable.q4, q4) != 0) return false;
        if (Double.compare(eotable.q5, q5) != 0) return false;
        if (Double.compare(eotable.q6, q6) != 0) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id;
        temp = Double.doubleToLongBits(head);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(sumN);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(n1);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(n2);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(n3);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(n4);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(n5);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(n6);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(sumQ);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(q1);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(q2);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(q3);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(q4);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(q5);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(q6);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

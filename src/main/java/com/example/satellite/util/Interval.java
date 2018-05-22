package com.example.satellite.util;

/**
 * Description: 区间
 * Created by Gaoxinwen on 2016/9/8.
 */
public class Interval {
    private double lower;
    private double upper;

    public Interval(double lower, double upper) {
        this.lower = lower;
        this.upper = upper;
    }

    public double getLower() {
        return lower;
    }

    public void setLower(double lower) {
        this.lower = lower;
    }

    public double getUpper() {
        return upper;
    }

    public void setUpper(double upper) {
        this.upper = upper;
    }

    @Override
    public String toString() {
        return "Interval{" +
                "lower=" + lower +
                ", upper=" + upper +
                '}';
    }

    /**
     * 获取区间范围
     * @return 范围
     */
    public double getRange() {
        return upper - lower;
    }

    /**
     * 得到一少半的区间
     * @param d 区间的一个数
     * @return 少半
     */
    public double getLessHalfRange(double d) {
        if (d < lower || d > upper) {
            throw new IllegalArgumentException(d + "is not in this interval.");
        }
        return Math.min(upper - d, d - lower);
    }
}

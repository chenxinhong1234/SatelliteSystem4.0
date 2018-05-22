package com.example.satellite.economicaloperation.pso;

import com.example.satellite.util.MathUtils;

/**
 * 位置向量，存储粒子的位置，n维-->数组长度n
 * 
 * @author: GXW
 * @date:   2016年3月10日
 */
public class Location {

    // UC问题即为机组台数*时段数（N*T）的二维数组，0/1变量
	private int[][] loc;

    public Location(int[][] loc) {
        this.loc = loc;
    }

    public int[][] getLoc() {
        return loc;
    }

    public void setLoc(int[][] loc) {
        this.loc = loc;
    }

    /**
     * 获取某个时段的机组组合
     * @param t 时段t，从0开始
     * @return uc
     */
    public int[] getUC(int t) {
        if (loc == null) {
            throw new IllegalArgumentException("Location is null");
        }

        return MathUtils.getColumn(loc, t);
    }
}

package com.example.satellite.util;

import com.example.satellite.station.HydroStation;

/**
 * Description:
 * Created by Gaoxinwen on 2016/9/23.
 */
public class Other {

    /**
     * 定出力水能计算，需要用到电站的出力系数，用来做厂内经济运行感觉很蠢！！！
     * @param periodCount 时段数
     * @param inflow 入库流量
     * @param beginLevel 初始水位
     * @param output 出力
     * @param station 电站
     * @return 【水头，末水位，下游水位】的数组
     */
    private static double[] getHead(int periodCount, double inflow, double beginLevel, double output, HydroStation
            station) {
        double endLevel, downLevel;
        double beginV, endV;
        double outflow;
        double head, output_temp;
        double A = 8.3; // 出力系数

        // 先令入库等于出库
        outflow = inflow;

        do {
            // 水量平衡
            beginV = station.getLevelCapacityCurve().getCapacityByLevel(beginLevel);
            // 库容的单位是什么？
            endV = beginV + (inflow - outflow) * 24 * 3600 / periodCount / 1E6;
            endLevel = station.getLevelCapacityCurve().getLevelByCapacity(endV);
            downLevel = station.getDownlevelDischargeCurve().getDownlevelByDischarge(outflow);
            head = (beginLevel + endLevel) / 2 - downLevel;
            // 出力的单位呢？
            output_temp = A * outflow * head / 1E4;
            outflow = output * 1E4 / A / head;
        } while (Math.abs(output - output_temp) > 0.01);

        return new double[] {head, endLevel, downLevel};

    }

    public static double divide(double a, double b) throws Exception {
        if (b == 0) {
            throw new Exception("除数不能为0");
        }

        return a / b;
    }
}

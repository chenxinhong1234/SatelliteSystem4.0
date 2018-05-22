package com.example.satellite.economicaloperation;

import com.example.satellite.domain.Eotable;
import com.example.satellite.station.HydroStation;
import com.example.satellite.station.unit.Unit;
import com.example.satellite.util.Interval;
import com.example.satellite.util.MathUtils;
import com.example.satellite.util.excel.WriteExcel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 最优负荷分配表：出力直接取整，不然处理余数太麻烦
 *
 * @author : GXW
 */
public class EconomicDispatchTable {

    private static WriteExcel ee = new WriteExcel();

    private static List<Eotable> eotableList = new ArrayList<>();

    private static int index = 1;

    private static final double ERROR = 0.001;

    /**
     * “以电定水”模块，给定水头，机组全开状态下的最优负荷分配表
     *
     * @param station 电站
     * @param head 水头
     */
    public static void createTable(HydroStation station, double head) {
        // 机组台数
        int unitCount = station.getUnitCount();
        // 所有机组
        List<Unit> unitList = station.getUnitList();

        // 某水头下机组的最大、最小出力
        double[] maxN = new double[unitCount];
        double[] minN = new double[unitCount];

        for (int i = 0; i < unitCount; i++) {
            // 某一水头下每台机组的预想出力取整
            maxN[i] = Math.floor(unitList.get(i).getHeadExceptedOutputCurve().getExceptedOutputByHead(head));
            minN[i] = 0;
        }
        /*
         * 输出参数，供测试使用
		 */
        System.out.println("机组台数为" + unitCount);
        System.out.println("水头为" + head);
        System.out.println("最大出力： " + Arrays.toString(maxN));
        System.out.println("最小出力： " + Arrays.toString(minN));

        // 步长：1MW
        double intervalN = 1.0;
        double sumN = 0;
        for (double aMaxN : maxN) sumN += aMaxN;
        System.out.println("总出力： " + sumN);


        // 不满足出力要求的流量惩罚值
        final double Q_PENALTY = 1E10;

		/*
         * 生成状态变量（unitCount台机组的总负荷）
		 */
        double[] stateVariable = MathUtils.discreteToArray(0, sumN, intervalN);

		/*
         * 动态规划顺向递推，求解计算
		 */
        // 返回的最优分配结果
        double[][] bestN = new double[stateVariable.length][unitCount];
        double[][] bestQ = new double[stateVariable.length][unitCount];

        // 中间存储结果
        double[][] optimalN = new double[unitCount][stateVariable.length];
        double[][] optimalQ = new double[unitCount][stateVariable.length];

        // 阶段变量，机组台数unitCount
        for (int i = 0; i < unitCount; i++) {
            // 机组振动区
            List<Interval> pozList = unitList.get(i).getPozList();
            // 状态变量，总负荷
            for (int j = 0; j < stateVariable.length; j++) {
                // 第一台机组
                if (i == 0) {
                    optimalN[i][j] = stateVariable[j];
                    // 不在机组出力上下限的加入惩罚
                    if (stateVariable[j] > maxN[i] || stateVariable[j] < minN[i]) {
                        optimalQ[i][j] = Q_PENALTY;
                    }
                    // 当机组出力在振动区时，流量乘以相应倍数作为振动区惩罚(1 + 少半*2/全部)
                    else if (isInPOZ(stateVariable[j], unitList.get(i).getPozList())) {
                        optimalQ[i][j] = unitList.get(i).getNhqCurve().getQByHN(head, stateVariable[j]) * (1 +
                                pozList.get(0).getLessHalfRange(stateVariable[j]) / pozList.get(0).getRange() * 2);
                    } else {
                        optimalQ[i][j] = unitList.get(i).getNhqCurve().getQByHN(head, stateVariable[j]);
                    }
                    // 后几台机组，顺向递推
                } else {
                    optimalQ[i][j] = Q_PENALTY;
                    // 决策变量，第i台机组的负荷
                    for (int k = 0; k <= j; k++) {
                        // 前面i - 1台机组j - k的出力对应的流量
                        double tempQ = optimalQ[i - 1][j - k];
                        // 第i台机组出力为k，总和为j
                        // 不满足第i台机组的出力上下限，加入惩罚
                        if (stateVariable[k] > maxN[i] || stateVariable[k] < minN[i]) {
                            tempQ = Q_PENALTY;

                        } else if (isInPOZ(stateVariable[k], unitList.get(i).getPozList())) {
                            double tempQQ = unitList.get(i).getNhqCurve().getQByHN(head, stateVariable[k]) * (1 +
                                    pozList.get(0).getLessHalfRange(stateVariable[k]) / pozList.get(0).getRange() * 2);
                            tempQ += tempQQ;
                        } else {
                            double tempQQ = unitList.get(i).getNhqCurve().getQByHN(head, stateVariable[k]);
                            tempQ += tempQQ;
                        }

                        /*
                         * 加入约束：柘林电厂必须开一台小机组才能开大机组
                         */
                        if (i >= 4 && k == j) {
                            tempQ = Q_PENALTY;
                        }

                        // 判断耗流量的大小，小的话就找到了较优解，赋值
                        // 有时候明明相等，但是因为数据存储的问题，一个大一个小，这时就要给定精度
                        if (optimalQ[i][j] - tempQ > ERROR) {
                            optimalQ[i][j] = tempQ;
                            optimalN[i][j] = stateVariable[k];
                        }
                    }
                }
            }
        }

		/*
         * 逆向递推，计算各状态变量下的最优出力
		 */
        for (int j = 0; j < stateVariable.length; j++) {
            double leaveValue = stateVariable[j];

            for (int i = unitCount - 1; i >= 0; i--) {
                if (i == unitCount - 1) {
                    bestN[j][i] = optimalN[i][j];
                    bestQ[j][i] = unitList.get(i).getNhqCurve().getQByHN(head, bestN[j][i]);
                } else {
                    leaveValue -= bestN[j][i + 1];
                    // 出力为leaveValue对应的下标为leaveValue / intervalN取整
                    bestN[j][i] = optimalN[i][(int) (leaveValue / intervalN)];
                    bestQ[j][i] = unitList.get(i).getNhqCurve().getQByHN(head, bestN[j][i]);
                }
            }
        }

		/*
         * 输出结果
		 */
        for (int i = 0; i < bestN.length; i++) {
            Eotable table = new Eotable();
            table.setId(index++);
            table.setHead(head);
            table.setN(bestN[i]);
            table.setSumN(MathUtils.getSum(bestN[i]));
            table.setQ(bestQ[i]);
            table.setSumQ(MathUtils.getSum(bestQ[i]));

            eotableList.add(table);
        }


    }

    /**
     * 获得所有水头下的最优负荷分配表
     * @param station 电站
     * @param beginHead 初始水头27m
     * @param endHead 末水头43m
     */
    public static void getAllTables(HydroStation station, double beginHead, double endHead) {
        double[] heads = MathUtils.discreteToArray(beginHead, endHead, 0.5);
        for (double head : heads) {
            createTable(station, head);
        }

//        try {
//            ee.write("", Eotable.class, "最优负荷分配表");
//            ee.setDataList(EconomicDispatchTable.eotableList);
//            ee.writeFile("C:/Users/Administrator/Desktop/table.xlsx");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    /**
     * 判断是否在振动区
     *
     * @param n   出力
     * @param pozList 振动区范围
     * @return 是否
     */
    public static boolean isInPOZ(double n, List<Interval> pozList) {
        if (pozList == null || n == 0)
            return false;

        for (Interval interval : pozList) {
            if (n > interval.getLower() && n < interval.getUpper()) {
                return true;
            }
        }
        return false;
    }

}

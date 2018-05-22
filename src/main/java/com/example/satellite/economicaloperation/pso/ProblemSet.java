package com.example.satellite.economicaloperation.pso;

import com.example.satellite.domain.Eotable;
import com.example.satellite.economicaloperation.DPUC;
import com.example.satellite.economicaloperation.Node;
import com.example.satellite.station.HydroStation;
import com.example.satellite.util.MathUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 具体问题
 *
 * @author: GXW
 * @date: 2016年3月10日
 */
public class ProblemSet {

    public static final double VEL_LOW = -10;
    public static final double VEL_HIGH = 10;

    // 最短开停机时间约束
    public static final int[] MUT = {2, 2, 2, 2, 4, 4};
    public static final int[] MDT = {2, 2, 2, 2, 4, 4};

//    public static final int[] MUT = {8, 8, 8, 8, 16, 16};
//    public static final int[] MDT = {8, 8, 8, 8, 16, 16};

    // 机组优先顺序，从1开始
    public static final int[] UNITPRIORITY = {1, 2, 3, 4, 5, 6};


    /**
     * 求适应度，返回详细结果（Node的List），总的适应值为最后一个node的fitness值
     *
     * @param location       机组组合
     * @param initialStatus  前一天最后一个时段的开停机状态
     * @param inflow         入库
     * @param beginLevel     初水位
     * @param output         给定出力
     * @param allEotableList 最优负荷分配表
     * @param station        电站
     * @return NodeList
     */
    public static List<Node> evaluate(int[][] location, int[] initialStatus, double[] inflow, double beginLevel,
                                      double[] output,
                                      List<List<Eotable>> allEotableList, HydroStation station) {

        List<Node> nodeList = new ArrayList<>();

        final int T = location[0].length;

        for (int t = 0; t < T; t++) {
            double fitness = 0;
            double levelTemp = (t == 0) ? beginLevel : nodeList.get(t - 1).getFinalLevel();
            int[] uc = MathUtils.getColumn(location, t);
            int[] temp = getEqualUC(uc);
            Node tempNode = DPUC.getHead(temp, T, inflow[t], levelTemp, output[t], allEotableList,
                    station);
            if (tempNode != null) {
                Node node = new Node();
                node.setUc(uc);
                node.setN(DPUC.changeByUC(temp, uc, tempNode.getN()));
                node.setQ(DPUC.changeByUC(temp, uc, tempNode.getQ()));
                node.setHead(tempNode.getHead());
                node.setFinalLevel(tempNode.getFinalLevel());

                fitness += MathUtils.getSum(node.getQ());

                node.setFitness(fitness);
                nodeList.add(node);
            }
        }

        return nodeList;
    }

    /**
     * 开停机耗水量
     * @param location 机组组合
     * @return cost
     */
    public static double getStartCost(int[][] location, int[] initialStatus) {

        final int N = location.length;
        final int T = location[0].length;

        int[] initialUC = new int[N];
        for (int i = 0; i < N; i++) {
            initialUC[i] = (initialStatus[i] > 0) ? 1 : 0;
        }

        double cost = 0;

        for (int t = 0; t < T; t++) {
            int[] previousUC;
            if (t == 0) {
                previousUC = initialUC;
            } else {
                previousUC = MathUtils.getColumn(location, t - 1);
            }
            int[] nowUC = MathUtils.getColumn(location, t);
            for (int n = 0; n < N; n++) {
                // 开机状态
                if (previousUC[n] == 0 && nowUC[n] == 1) {
                    cost += 0;
                }
                // 停机状态
                if (previousUC[n] == 1 && nowUC[n] == 0) {
                    cost += 0;
                }
            }
        }

        return cost;
    }

    /**
     * 获取与原机组组合等价的uc，小机组与大机组都从第一台开始开（011001-->110010）
     *
     * @param uc 原机组组合
     * @return uc
     */
    public static int[] getEqualUC(int[] uc) {
        int small = 0;
        int big = 0;
        for (int i = 0; i < uc.length; i++) {
            if (i < 4 && uc[i] == 1) {
                small++;
            }
            if (i >= 4 && uc[i] == 1) {
                big++;
            }
        }

        int[] temp = new int[uc.length];
        for (int i = 0; i < small; i++) {
            temp[i] = 1;
        }
        for (int i = 0; i < big; i++) {
            temp[4 + i] = 1;
        }

        return temp;
    }
}

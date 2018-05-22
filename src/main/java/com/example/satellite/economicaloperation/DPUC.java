package com.example.satellite.economicaloperation;

import com.example.satellite.domain.Eotable;
import com.example.satellite.station.HydroStation;
import com.example.satellite.station.unit.Unit;
import com.example.satellite.util.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Description: 动态规划计算机组组合优化问题
 * Created by Gaoxinwen on 2016/9/6.
 */
public class DPUC {

    private static final double ERROR = 0.001;

    public static List<Node> unitCommitment(int periodCount, double[] load, double[] inflow, double beginLevel,
                                      int[] schedule, HydroStation station, List<List<Eotable>> allEotableList) {

        /*
         * 需考虑前面时段的开停机及机组组合状况
         */
        Node first = new Node();
        // 96时段
        first.setUc(new int[]{1, 1, 1, 1, 1, 1});
        first.setDuration(new int[]{8, 8, 8, 8, 16, 16});
        final int[] MUT = {8, 8, 8, 8, 16, 16};
        final int[] MDT = {8, 8, 8, 8, 16, 16};

        // 24时段
//        first.setUc(new int[]{1, 1, 1, 1, 1, 1});
//        first.setDuration(new int[]{2, 2, 2, 2, 4, 4});
//        final int[] MUT = {2, 2, 2, 2, 4, 4};
//        final int[] MDT = {2, 2, 2, 2, 4, 4};


        // 先定一类机组组合（小机组开几台，大机组开几台），给定一个末水位（等价于一个下泄流量），得到水头，查表插值的到给定机组组合与水头下的负荷分配情况及耗流量
        // 重新计算末水位，看二者是否相等，否则按算出的末水位重复上述步骤，直到水位相等


        // 所有可能的机组组合
        List<List<int[]>> allUC = getAllUC();

        // 开辟空间 第一维：时段数t;第二维：可行机组组合
        List<List<Node>> allNodeList = new ArrayList<>();

        // DP计算
        // allUC里面包括全部停机的情况，全部停机但出力不为0的话，temp为null
        for (int t = 0; t < periodCount; t++) {
            System.out.println("第" +  (t + 1) + "时段");

            List<Node> nodeList = new ArrayList<>();

            if (t == 0) {
                for (List<int[]> combinedUC : allUC) {
                    // 确定这一类组合下的水头，需通过试算确定，水头试算的过程中需要查表，计算完成后得到result
                    // 出力约束也是在这里面检查的吧

                    Node temp = getHead(combinedUC.get(0), periodCount, inflow[t], beginLevel, load[t],
                            allEotableList, station);
                    if (temp == null) {
                        continue;
                    }

                    for (int[] uc : combinedUC) {
                        // 检修计划约束
                        if (scheduleCheck(uc, schedule)) {
                            // 最小开停机约束
                            int[] duration = durationCheck(first.getDuration(), first.getUc(), uc, MUT, MDT);
                            if (duration == null) {
                                continue;
                            }
                            Node node = new Node();
                            node.setUc(uc);
                            node.setDuration(duration);
                            // 机组组合变化，相应的出力流量分配也要跟着变化
                            node.setN(changeByUC(combinedUC.get(0), uc, temp.getN()));
                            node.setQ(changeByUC(combinedUC.get(0), uc, temp.getQ()));
                            node.setEnabled(true);
                            node.setPrevious(first);
                            node.setHead(temp.getHead());
                            node.setFinalLevel(temp.getFinalLevel());
                            double fitness = MathUtils.getSum(node.getQ());
                            fitness += getWaterConsumption(first.getUc(), node.getUc());
                            node.setFitness(fitness);
                            nodeList.add(node);
                        }
                    }
                }
            } else {

                // 后一时段，两个循环
                for (List<int[]> combinedUC : allUC) {

                    // 后面状态的确定需要用到前一状态的
                    /**
                     * 前一时段的循环，计入开停机损失，反映耗水量，逻辑有问题
                     */

                    // （）括号指定长度
                    List<List<Node>> combinedNode = new ArrayList<>(combinedUC.size());
                    for (int i = 0; i < combinedUC.size(); i++) {
                        combinedNode.add(new ArrayList<>());
                    }

                    for (Node previous : allNodeList.get(t - 1)) {

                        // 对应的前一个时段的末水位
                        Node temp = getHead(combinedUC.get(0), periodCount, inflow[t],
                                previous.getFinalLevel(), load[t], allEotableList, station);
                        if (temp == null) {
                            continue;
                        }
                        for (int j = 0; j < combinedUC.size(); j++) {
                            int[] uc = combinedUC.get(j);
                            // 检修计划约束
                            if (scheduleCheck(uc, schedule)) {
                                // 最小开停机约束
                                int[] duration = durationCheck(previous.getDuration(), previous.getUc(), uc, MUT, MDT);
                                if (duration == null) {
                                    continue;
                                }
                                Node node = new Node();
                                node.setUc(uc);
                                node.setDuration(duration);
                                node.setN(changeByUC(combinedUC.get(0), uc, temp.getN()));
                                node.setQ(changeByUC(combinedUC.get(0), uc, temp.getQ()));
                                node.setEnabled(true);
                                node.setPrevious(previous);
                                node.setHead(temp.getHead());
                                node.setAflow(temp.getAflow());
                                node.setFinalLevel(temp.getFinalLevel());
                                double fitness = previous.getFitness() + temp.getFitness();
                                fitness += getWaterConsumption(previous.getUc(), node.getUc());
                                node.setFitness(fitness);

                                combinedNode.get(j).add(node);

                            }
                        }
                    }

                    // 找到耗水量最小的Node（previous起作用）
                    combinedNode.stream().filter(nodes -> !nodes.isEmpty()).forEach(nodes -> {
                        // ASC，找第一个
                        nodes.sort(Comparator.comparingDouble(Node::getFitness));
                        nodeList.add(nodes.get(0));
                    });
                }
            }

            allNodeList.add(nodeList);
        }

        // 回溯求最优解
        List<Node> resultList = new ArrayList<>();
        int t = periodCount - 1;
        List<Double> lastFitness = allNodeList.get(t).stream().map(Node::getFitness).collect(Collectors.toList());
        int minIndex = MathUtils.getMinIndex(lastFitness);
        resultList.add(allNodeList.get(t).get(minIndex));
        while (t > 0) {
            resultList.add(resultList.get(periodCount - 1 - t).getPrevious());
            t--;
        }
        // 逆序
        Collections.reverse(resultList);

        return resultList;
    }

    /**
     * 给定机组组合，查表试算水头，并得到该水头下的最优负荷分配表结果
     * @param uc 机组组合
     * @param periodCount 时段数
     * @param inflow 入库
     * @param beginLevel 初水位
     * @param output 出力
     * @param station 电站
     * @return Node
     */
    public static Node getHead(int[] uc, int periodCount, double inflow, double beginLevel, double output,
                                List<List<Eotable>> allEotableList, HydroStation station) {
        double endLevel, downLevel;
        double beginV, endV;
        double outflow, outflow_temp, aflow = 0;
        double head;
        Eotable eotable;

        // 先令入库等于出库
        outflow = inflow;

        int index = 0;
        do {
            outflow_temp = outflow;
            // 水量平衡
            beginV = station.getLevelCapacityCurve().getCapacityByLevel(beginLevel);
            // 库容的单位是什么？
            endV = beginV + (inflow - outflow_temp) * 24 * 3600 / periodCount / 1E6;
            endLevel = station.getLevelCapacityCurve().getLevelByCapacity(endV);
            // 水位约束限制
            // 水位超过正常蓄水位弃水，小于死水位则少发电
            if (endLevel > station.getStationinfo().getNormallevel()) {
                endLevel = station.getStationinfo().getNormallevel();
                aflow = (endV - station.getLevelCapacityCurve().getCapacityByLevel(endLevel)) * 1E6 / 24 / 3600
                        * periodCount;

            } else if (endLevel < station.getStationinfo().getDeadlevel()) {

            }
            downLevel = station.getDownlevelDischargeCurve().getDownlevelByDischarge(outflow_temp);
            head = (beginLevel + endLevel) / 2 - downLevel;
            // 出力的单位呢？
            // 查表计算该水头下的机组耗流
            // 相邻水头插值还是有问题，直接取最近的水头吧！！！
            eotable = queryTable(uc, MathUtils.mRound(head, 0.5), output, allEotableList, station);
            eotable = changeTable(uc, eotable);
            if (eotable == null) {
                return null;
            }
            outflow = eotable.getSumQ();
            index++;

        } while ((Math.abs(outflow - outflow_temp) > 1) && (index < 50));

        Node node = new Node();
        node.setHead(head);
        node.setN(eotable.findNArray());
        node.setQ(eotable.findQArray());
        node.setAflow(aflow);
        node.setFinalLevel(endLevel);
        node.setFitness(MathUtils.getSum(node.getQ()));

        return node;
    }

    /**
     * 获取组合可行区的范围（组合振动区求补集）
     * uc[i]为1的机组必须开机，但是也可能空载？？？已在mergeUnit方法中修改！！！
     * 按理应该可以不开机，但是又无法避免出现某台小机组空载，开两台大机组的情况，暂时先不修改
     * @param uc 机组组合
     * @param head 水头
     * @param station 水电站
     * @return 组合可行区
     */
    public static List<Interval> getFeasibleZone(int[] uc, double head, HydroStation station) {
        List<Unit> unitList = new ArrayList<>();

        for (int i = 0; i < uc.length; i++) {
            if (uc[i] == 1) {
                unitList.add(station.getUnitById(i + 1));
            }
        }

        if (unitList.size() == 1) {
            List<Interval> intervals = unitList.get(0).getPozList();
            Interval domain = new Interval(0, Math.floor(unitList.get(0).getHeadExceptedOutputCurve()
                    .getExceptedOutputByHead(head)));
            return IntervalUtils.complement(intervals, domain);
        } else {

            List<IntervalCustom> customList = new ArrayList<>();
            for (Unit unit : unitList) {
                List<Interval> intervals = unit.getPozList();
                Interval domain = new Interval(0, unit.getHeadExceptedOutputCurve().getExceptedOutputByHead
                        (head));
                customList.add(new IntervalCustom(intervals, domain));
            }

            // 两台机组振动区组合，多于两台的转化成两台计算
            IntervalCustom temp = customList.get(0);
            for (int i = 0; i < customList.size() - 1; i++) {
                temp = IntervalUtils.mergeUnit(temp, customList.get(i + 1));
            }

            return IntervalUtils.complement(temp.getIntervals(), temp.getDomain());

        }
    }


    /**
     * 获得所有的机组组合，只适合柘林电厂，没有做统一化处理。全部停机也可以吧！
     * @return 第一维：组合机组（如小机组开2台，大机组开1台）；第二维：所有具体的开机台数（如110010,101010）
     */
    private static List<List<int[]>> getAllUC() {

        List<List<int[]>> allUC = new ArrayList<>();
        // 柘林共6台机组，其中小机组四台，大机组两台
        for (int i = 0; i <= 2; i++) {
            for (int j = 0; j <= 4; j++) {
                // 柘林开大机组必须先开一台小机组，可以全部停机
                if (j == 0 && i != 0)
                    continue;
                List<String> uc1 = Permutations.permutate(j, 4);
                List<String> uc2 = Permutations.permutate(i, 2);
                // 合并机组组合（1100 + 10 = 110010）
                List<int[]> combinedUC = new ArrayList<>();
                for (String s1 : uc1) {
                    combinedUC.addAll(uc2.stream().map(s2 -> Permutations.convertToIntArray(s1 + s2)).collect(Collectors.toList()));
                }
                allUC.add(combinedUC);
            }
        }

        return allUC;
    }

    /**
     * 机组组合变化后，出力流量跟着变化（前面四台小机组，后面两台大机组）
     * @param uc1 原机组组合
     * @param uc2 变化后的机组组合
     * @param array 原数组
     * @return 变化后的数组
     */
    public static double[] changeByUC(int[] uc1, int[] uc2, double[] array) {
        if (Arrays.equals(uc1, uc2)) {
            return array;
        } else {
            double[] result = new double[array.length];
            int index = 0;
            for (int i = 0; i < 4; i++) {
                if (uc1[i] == 1) {
                    for (int j = index; j < 4; j++) {
                        if (uc2[j] == 1) {
                            result[j] = array[i];
                            index = j + 1;
                            break;
                        }
                    }
                }
            }

            index = 4;
            for (int i = 4; i < 6; i++) {
                if (uc1[i] == 1) {
                    for (int j = index; j < 6; j++) {
                        if (uc2[j] == 1) {
                            result[j] = array[i];
                            index = j + 1;
                            break;
                        }
                    }
                }
            }
            return result;
        }
    }

    /**
     * 检修计划约束
     * @param uc 机组组合
     * @param schedule 检修计划：1代表检修，0代表正常
     * @return 是否满足计划
     */
    private static boolean scheduleCheck(int[] uc, int[] schedule) {
        for (int i = 0; i < uc.length; i++) {
            if (schedule[i] == 1 && uc[i] == 1) {
                return false;
            }
        }
        return true;
    }

    /**
     * 预想出力约束
     * @param uc 机组组合
     * @param station 电站
     * @param load 出力
     * @param head 水头
     * @return 给定出力是否满足预想出力约束
     */
    private static boolean loadCheck(int[] uc, HydroStation station, double load, double head) {
        double temp = 0;
        for (int i = 0; i < uc.length; i++) {
            if (uc[i] == 1) {
                temp += station.getUnitById(i + 1).getHeadExceptedOutputCurve().getExceptedOutputByHead(head);
            }
        }
        return load <= temp;
    }


    /**
     * 设置空载的机组
     * @param uc 机组组合
     * @param result 原结果
     * @return 现结果
     */
    private static Eotable changeTable(int[] uc, Eotable result) {
        if (result != null) {
            // find方法是新建一个数组，需要再set一下
            double[] n = result.findNArray();
            double[] q = result.findQArray();
            // 将uc为0的机组强制置为0
            for (int j = 0; j < uc.length; j++) {
                if (uc[j] == 0) {
                    n[j] = 0;
                    q[j] = 0;
                }
                if (uc[j] == 1 && Math.abs(n[j])  < ERROR) {
                    if (j < 4) {
                        q[j] = 10;
                    } else {
                        q[j] = 20;
                    }

                }
            }
            result.setN(n);
            result.setSumN(MathUtils.getSum(n));
            result.setQ(q);
            result.setSumQ(MathUtils.getSum(q));
        }

        return result;
    }


    /**
     * 任意机组之间的最优负荷分配: 利用最优性原理推广定理，在机组全开状态下空间最优负荷分配总表的基础上，
     * 获得任意机组组合情形下的最优负荷分配结果，不满足组合振动区要求的话返回null.
     * 机组全开状态下的最优负荷分配表出力为0的表示空载，不应该是停机！！！（要有资料）
     * 空载的话流量不为0，机组组合中0的是停机，流量为0，需要处理，暂时未处理！！！
     * 可以处理出力为0的情况
     * 若最优负荷分配表中有些机组出力在振动区（不可避免），也能查出来
     * 表中水头以0.5m离散，需要插值进行计算；相邻两个水头下的出力分配有微小差别
     * ***需要判断是否满足预想出力要求！！！
     * @param uc ： 机组组合
     * @param head 水头
     * @param sumN ：总出力
     * @return Eotable
     */
    private static Eotable queryTable(int[] uc, double head, double sumN, List<List<Eotable>> allEotableList,
                                      HydroStation station) {

        // 先进行出力约束检查，不满足直接返回null
        if (!loadCheck(uc, station, sumN, head)) {
            return null;
        }


        // 水头约束
        // 如果head小于最小水头，取最小水头；head大于最大水头，取最大水头
        List<Eotable> eotableList = null;
        int size = allEotableList.size();
        if (head < allEotableList.get(0).get(0).getHead()) {
            eotableList = allEotableList.get(0);
        } else if (head > allEotableList.get(size - 1).get(0).getHead()) {
            eotableList = allEotableList.get(size - 1);
        } else {
            for (List<Eotable> list : allEotableList) {
                // 相等
                if (Math.abs(list.get(0).getHead() - head) < ERROR ) {
                    eotableList = list;
                    break;
                }
            }
        }

        if (eotableList == null) {
            return null;
        }

        // 出力取整，查最优负荷分配表中该出力对应的那一条结果
        int index = (int) sumN;

        // 利用repository从数据库读取的数据不要乱set，一旦set，就会改变数据库里面的值！！！
        // 但是可使用clone()方法,前提是域为普通变量，否则必须进行深克隆
        Eotable result = new Eotable();

        for (int i = index; i < eotableList.size(); i++) {
            double sumN_temp = 0;
            Eotable eotable = eotableList.get(i);
            for (int j = 0; j < uc.length; j++) {
                sumN_temp += eotable.findNArray()[j] * uc[j];
            }

            // 正好找到了指定机组组合下的最优负荷分配，需处理小数
            // 相等
            /**
             * 还要将uc中为1但是出力为0的机组定为空载
             */
            if (Math.abs(sumN_temp - Math.floor(sumN)) < ERROR) {

                if (Math.abs(sumN - Math.floor(sumN)) < ERROR) {
                    // clone一下
                    try {
                        result = (Eotable) eotable.clone();
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        // 出力取比原出力大1MW的那一行，找出出力最大的机组，使之出力降低一点
                        result = (Eotable) eotableList.get(i + 1).clone();
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                    double[] n = result.findNArray();
                    int maxIndex = MathUtils.getMaxIndex(n);
                    n[maxIndex] -= (sumN - Math.floor(sumN));
                    result.setN(n);
                    result.setSumN(MathUtils.getSum(n));
                    double[] q = result.findQArray();
                    q[maxIndex] = station.getUnitById(maxIndex + 1).getNhqCurve().getQByHN(head, n[maxIndex]);
                    result.setQ(q);
                    result.setSumQ(MathUtils.getSum(q));
                }
                return result;
            } else if (sumN_temp - Math.floor(sumN) > ERROR) {


                // 可行
                if (IntervalUtils.contains(Math.floor(sumN), getFeasibleZone(eotable.findUC(), head, station))) {

                    // 可以插值，仅适用于相近的两个组合一致的情况
                    Eotable previous = eotableList.get(i - 1);
                    if (Arrays.equals(eotable.findUC(), previous.findUC())) {
                        double[] n1 = previous.findNArray();
                        double[] q1 = previous.findQArray();
                        double[] n2 = eotable.findNArray();
                        double[] q2 = eotable.findQArray();

                        double[] n = new double[n1.length];
                        double[] q = new double[n2.length];

                        // 上一行uc组合对应的出力和
                        double previousSumN = 0;
                        for (int k = 0; k < uc.length; k++) {
                            previousSumN += previous.findNArray()[k] * uc[k];
                        }

                        for (int k = 0; k < n.length; k++) {
                            n[k] = (sumN - previousSumN) / (sumN_temp - previousSumN) * (n2[k] - n1[k]) + n1[k];
                            q[k] = (sumN - previousSumN) / (sumN_temp - previousSumN) * (q2[k] - q1[k]) + q1[k];
                        }

                        result.setN(n);
                        result.setSumN(MathUtils.getSum(n));
                        result.setQ(q);
                        result.setSumQ(MathUtils.getSum(q));
                    } else {
                        // 直接使用动态规划重新计算,太慢了
//                        result = EconomicDispatch.dispatchUnitN(station, uc, head, sumN);
                        // 只是不能线性插值罢了，先全部赋值成最小值，再使机组出力变大（先大机组再小机组）直到出力相等


                        double[] n = new double[uc.length];
                        int[] priority = {1, 2, 3, 4, 5, 6};

                        // 预处理。。。。。。。。。
                        for (int k = 4; k < uc.length; k++) {
                            if (uc[k] == 1) {
                                n[k] = station.getUnitById(k + 1).getPozList().get(0).getUpper();
                            }
                        }
                        if (MathUtils.getSum(n) > sumN) {
                            n[5] = 0;
                        }
                        if (MathUtils.getSum(n) > sumN) {
                            n[4] = 0;
                        }


                        for (int k = 0; k < uc.length; k++) {
                            if (uc[priority[k] - 1] == 1) {
                                n[priority[k] - 1] = Math.floor(station.getUnitById(priority[k]).getHeadExceptedOutputCurve()
                                        .getExceptedOutputByHead(head));
                                // 直接处理小数
                                if (sumN <= MathUtils.getSum(n)) {
                                    n[priority[k] - 1] += (sumN - MathUtils.getSum(n));
                                    break;
                                }
                            }
                        }

                        // 重新计算耗水量
                        // clone一下
                        try {
                            result = (Eotable) eotable.clone();
                        } catch (CloneNotSupportedException e) {
                            e.printStackTrace();
                        }
                        result.setN(n);
                        result.setSumN(MathUtils.getSum(n));
                        double[] q = new double[n.length];
                        for (int k = 0; k < q.length; k++) {
                            if (uc[k] == 1) {
                                q[k] = station.getUnitById(k + 1).getNhqCurve().getQByHN(head, n[k]);
                            }
                        }
                        result.setQ(q);
                        result.setSumQ(MathUtils.getSum(q));
                    }

                    return result;

                } else {
                    return null;
                }
            }

        }
        return null;
    }

//    /**
//     * 先计算相邻两个水头下的Eotable，在进行插值，有可能返回空，表示不可行
//     * @return Eotable
//     */
//    private static Eotable query(int[] uc, double head, double sumN, List<List<Eotable>> allEotableList,
//                                 HydroStation station) {
//
//        // 水头以0.5m进行离散，获取下限值
//        double headFloor = MathUtils.mFloor(head, 0.5);
//
//        Eotable result;
//
//        // 相等不需要插值
//        if (Math.abs(headFloor - head) < ERROR) {
//            result =  queryTable(uc, headFloor, sumN, allEotableList, station);
//        } else {
//
//            // 这个插值也会出问题
//
//            Eotable eotableFloor = queryTable(uc, headFloor, sumN, allEotableList, station);
//            double headCeil = MathUtils.mCeil(head, 0.5);
//            Eotable eotableCeil = queryTable(uc, headCeil, sumN, allEotableList, station);
//
//            // 不为空的话进行插值
//            if (eotableFloor != null && eotableCeil != null) {
//
//                if (eotableCeil.getSumQ() < 0) {
//                    System.out.println("???");
//                }
//                result = new Eotable();
//                double[] n1 = eotableFloor.findNArray();
//                double[] q1 = eotableFloor.findQArray();
//                double[] n2 = eotableCeil.findNArray();
//                double[] q2 = eotableCeil.findQArray();
//
//                double[] n = new double[n1.length];
//                double[] q = new double[n2.length];
//
//                for (int i = 0; i < n.length; i++) {
//                    n[i] = (head - headFloor) / (headCeil - headFloor) * (n2[i] - n1[i]) + n1[i];
//                    q[i] = (head - headFloor) / (headCeil - headFloor) * (q2[i] - q1[i]) + q1[i];
//                }
//
//                result.setN(n);
//                result.setSumN(MathUtils.getSum(n));
//                result.setQ(q);
//                result.setSumQ(MathUtils.getSum(q));
//
//            } else {
//                return null;
//            }
//        }
//
//        if (result != null) {
//            // find方法是新建一个数组，需要再set一下
//            double[] n = result.findNArray();
//            double[] q = result.findQArray();
//            // 将uc为0的机组强制置为0
//            for (int i = 0; i < uc.length; i++) {
//                if (uc[i] == 0) {
//                    n[i] = 0;
//                    q[i] = 0;
//                }
//                if (uc[i] == 1 && Math.abs(n[i])  < ERROR) {
//                    q[i] = 0;
//                }
//            }
//            result.setN(n);
//            result.setSumN(MathUtils.getSum(n));
//            result.setQ(q);
//            result.setSumQ(MathUtils.getSum(q));
//        }
//
//        return result;
//
//    }

    /**
     * 最小开停机时间约束
     * @param duration 开停机持续时间
     * @param uc 前一时段的uc
     * @param ucNext 后一时段的uc
     * @param MUT 最短开机时间
     * @param MDT 最短停机时间
     * @return  若满足约束，返回开停机持续时间，否则返回空
     */
    private static int[] durationCheck(int[] duration, int[] uc, int[] ucNext, int[] MUT, int[] MDT) {

        int[] durationTemp = new int[duration.length];
        System.arraycopy(duration, 0, durationTemp, 0, duration.length);

        for (int i = 0; i < uc.length; i++) {
            if (uc[i] == 1 && ucNext[i] == 1) {
                durationTemp[i]++;
            } else if (uc[i] == 1 && ucNext[i] == 0) {
                if (durationTemp[i] >= MUT[i]) {
                    durationTemp[i] = -1;
                } else {
                    return null;
                }
            } else if (uc[i] == 0 && ucNext[i] == 1) {
                if (-durationTemp[i] >= MDT[i]) {
                    durationTemp[i] = 1;
                } else {
                    return null;
                }
            } else {
                durationTemp[i]--;
            }
        }

        return durationTemp;
    }

    /**
     * 开停机耗水量
     * @param ucPrevious 前一时段uc
     * @param uc 当前时段uc
     * @return 开停机耗水量，没有开停机则为0
     */
    private static double getWaterConsumption(int[] ucPrevious, int[] uc) {
        double fitness = 0;
        for (int i = 0; i < uc.length; i++) {
            // 开机耗水量
            if (ucPrevious[i] == 0 && uc[i] == 1) {
                fitness += 0;
            }
            // 停机耗水量
            if (ucPrevious[i] == 0 && uc[i] == 1) {
                fitness += 0;
            }
        }
        return fitness;
    }

}

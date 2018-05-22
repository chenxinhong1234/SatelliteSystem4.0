package com.example.satellite.economicaloperation.hs;

import com.example.satellite.domain.Eotable;
import com.example.satellite.economicaloperation.DPUC;
import com.example.satellite.economicaloperation.Node;
import com.example.satellite.economicaloperation.pso.PSOProcess;
import com.example.satellite.economicaloperation.pso.ProblemSet;
import com.example.satellite.station.HydroStation;
import com.example.satellite.util.MathUtils;

import java.util.*;

public class HSProcess implements HSConstants {
    private List<Harmony> harmonyMemory = new ArrayList<>();
    private double[] fitnessValueList = new double[HMS];
    private Harmony bestHarmony = new Harmony();

    private Random generator = new Random();

    public List<Node> execute(double[] inflow, double beginLevel, double[] output,
                        List<List<Eotable>> allEotableList, HydroStation station) {

        int[] initialStatus = {2, 2, 2, 2, 4, 4};
//        int[] initialStatus = {8, 8, 8, 8, 16, 16};

        // 水头就是为了得到该水头下的预想出力，一天之内水头的变化不大，故可固定为某一水头，暂时用平均出力对应水头代替
        double outputAverage = MathUtils.getAverage(output);
        double inflowAverage = MathUtils.getAverage(inflow);
        int[] tempUC = {1, 1, 1, 1, 1, 1};
        int periodCount = output.length;
        Node node = DPUC.getHead(tempUC, periodCount, inflowAverage, beginLevel, outputAverage, allEotableList, station);
        if (node == null) {
            throw new IllegalArgumentException("Head is unsatisfactory!");
        }
        double head = node.getHead();

        // 问题维度
        final int N = station.getUnitCount();
        final int T = 24;

        // 初始化和声记忆库
        initializeHM(N, T, station, head, output, initialStatus);

        // 计算适应度
        updateFitnessList(initialStatus, inflow, beginLevel, output, allEotableList, station);
        System.out.println(Arrays.toString(fitnessValueList));

        // 最差和最好的个体
        int bestPos = MathUtils.getMinIndex(fitnessValueList);

        bestHarmony.setLocation(harmonyMemory.get(bestPos).getLocation());

        int t = 0;

        final double HMCR_MAX = 0.9;
        final double HMCR_MIN = 0.6;
        final double PAR_MAX = 0.99;
        final double PAR_MIN = 0.3;

        while (t < MAX_ITERATION) {

            List<Harmony> harmonyList = new ArrayList<>();
            harmonyList.addAll(harmonyMemory);

            double HMCR = HMCR_MAX - (double) t / MAX_ITERATION * (HMCR_MAX - HMCR_MIN);
            double PAR = PAR_MIN + (double) t / MAX_ITERATION * (PAR_MAX - PAR_MIN);

            double[] newFitnessList = new double[nNew];
            for (int n = 0; n < nNew; n++) {
                int[][] newLoc = new int[N][T];
                for (int i = 0; i < N; i++) {
                    for (int j = 0; j < T; j++) {
                        if (generator.nextDouble() < HMCR) {
                            newLoc[i][j] = harmonyMemory.get(generator.nextInt(HMS)).getLocation()[i][j];
                            if (generator.nextDouble() < PAR) {
                                // Global-best harmony search
                                newLoc[i][j] = bestHarmony.getLocation()[i][j];
                            }
                        } else {
                            newLoc[i][j] = (generator.nextDouble() < 0.5) ? 1 : 0;
                        }
                    }
                }

                // 修复策略
                PSOProcess.repair(newLoc, station, head, output, initialStatus);

                Harmony newHarmony = new Harmony();
                newHarmony.setLocation(newLoc);

                harmonyList.add(newHarmony);
                newFitnessList[n] = newHarmony.getFitnessValue(initialStatus, inflow, beginLevel, output,
                        allEotableList, station);

            }

            // 随机联赛选择
            Map<Integer, Double> map = new TreeMap<>();
            for (int i = 0; i < HMS; i++) {
                map.put(i, fitnessValueList[i]);
            }

            for (int i =0; i < nNew; i++) {
                map.put(i + HMS, newFitnessList[i]);
            }
            Map<Integer, Double> sortedMap = MathUtils.sortByValue(map);
            List<Harmony> newHarmonyMemory = new ArrayList<>();
            int index = 0;
            for (Map.Entry<Integer, Double> entry : sortedMap.entrySet()) {
                newHarmonyMemory.add(harmonyList.get(entry.getKey()));
                fitnessValueList[index] = entry.getValue();
                index++;
                if (index >= HMS)
                    break;
            }

            harmonyMemory = newHarmonyMemory;

            // 重新得到最优和声
            int bestLoc = MathUtils.getMinIndex(fitnessValueList);
            if (fitnessValueList[bestLoc] < bestHarmony.getFitnessValue(initialStatus, inflow, beginLevel, output,
                    allEotableList, station)) {
                bestHarmony.setLocation(harmonyMemory.get(bestLoc).getLocation());
            }

            t++;

            System.out.println("ITERATION " + t + ": ");
            System.out.println("     Value: " + bestHarmony.getFitnessValue(initialStatus, inflow, beginLevel, output,
                    allEotableList, station));
        }

        System.out.println("\nSolution found at iteration " + (t - 1) + ", the solution is:");
        System.out.println("     Best solution: " + Arrays.toString(bestHarmony.getLocation()));
        System.out.println("     Value: " + bestHarmony.getFitnessValue(initialStatus, inflow, beginLevel, output,
                allEotableList, station));

        return ProblemSet.evaluate(bestHarmony.getLocation(), initialStatus, inflow, beginLevel, output, allEotableList,
                station);
    }

    /**
     * 初始化和声库
     */
    public void initializeHM(int N, int T, HydroStation station, double head, double[] dailyLoad,
                             int[] initialStatus) {
        for (int i = 0; i < HMS; i++) {
            Harmony harmony = new Harmony();

            // randomize location inside a space defined in Problem Set
            int[][] loc = new int[N][T];
            for (int j = 0; j < loc.length; j++) {
                for (int k = 0; k < loc[j].length; k++) {
                    loc[j][k] = (generator.nextDouble() < 0.5) ? 0 : 1;
                }
            }

            // 最短开停机时间，系统备用约束，修复策略！
            PSOProcess.repair(loc, station, head, dailyLoad, initialStatus);

            harmony.setLocation(loc);

            harmonyMemory.add(harmony);

        }
    }

    public void updateFitnessList(int[] initialStatus, double[] inflow, double beginLevel, double[] output,
                                  List<List<Eotable>> allEotableList, HydroStation station) {
        for (int i = 0; i < HMS; i++) {
            fitnessValueList[i] = harmonyMemory.get(i).getFitnessValue(initialStatus, inflow, beginLevel, output,
                    allEotableList, station);
        }
    }


}

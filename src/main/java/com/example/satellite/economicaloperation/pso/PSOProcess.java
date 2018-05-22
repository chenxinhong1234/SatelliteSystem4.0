package com.example.satellite.economicaloperation.pso;

import com.example.satellite.domain.Eotable;
import com.example.satellite.economicaloperation.DPUC;
import com.example.satellite.economicaloperation.Node;
import com.example.satellite.station.HydroStation;
import com.example.satellite.util.IntervalUtils;
import com.example.satellite.util.MathUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class PSOProcess implements PSOConstants {

    // 粒子群
    private List<Particle> swarm = new ArrayList<>();
    // 个体极值
    private double[] pBest = new double[SWARM_SIZE];
    private List<Location> pBestLocation = new ArrayList<>();
    // 全局极值
    private double gBest;
    private Location gBestLocation;
    // 适应度
    private double[] fitnessValueList = new double[SWARM_SIZE];

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

		/*
         * 粒子群初始化
		 */
        initializeSwarm(N, T, station, head, output, initialStatus);

		/*
         * 计算初始粒子的适应度
		 */
        updateFitnessList(initialStatus, inflow, beginLevel, output, allEotableList, station);

		/*
         * 存储pBest and pBestLocation
		 */
        for (int i = 0; i < SWARM_SIZE; i++) {
            pBest[i] = fitnessValueList[i];
            pBestLocation.add(swarm.get(i).getLoc());
        }
        System.out.println("ITERATION " + 0 + ": ");
        System.out.println(MathUtils.getMin(fitnessValueList));

		/*
		 * 开始迭代，直到满足最大迭代次数，跳出循环
		 */
        // 迭代次数
        int t = 0;
        while (t < MAX_ITERATION) {

            if (t == MAX_ITERATION - 1) {
                System.out.println(t);
            }
            // step 1 - update pBest
            for (int i = 0; i < SWARM_SIZE; i++) {
                if (fitnessValueList[i] < pBest[i]) {
                    pBest[i] = fitnessValueList[i];
                    pBestLocation.set(i, swarm.get(i).getLoc());
                }
            }

			/*
			 * 不管是pBestLocation(List)还是gBestLocation，每次迭代都是新建一个Location，然后通过set方法赋值给Particle，不会影响
			 * 原来Location的值。
			 * http://www.javaworld.com/article/2077424/learn-java/does-java-pass-by-reference-or-pass-by-value.html
			 * http://tutorials.jenkov.com/java-concurrency/java-memory-model.html
			 */


            // step 2 - update gBest
            int bestParticleIndex = MathUtils.getMinIndex(fitnessValueList);
            // gBest没有初始化，但作为类的成员，double类型的默认值为0.0d
            if (t == 0 || fitnessValueList[bestParticleIndex] < gBest) {
                gBest = fitnessValueList[bestParticleIndex];

                if (gBestLocation == null) {
                    gBestLocation = new Location(swarm.get(bestParticleIndex).getLoc().getLoc());
                } else {
                    gBestLocation.setLoc(swarm.get(bestParticleIndex).getLoc().getLoc());
                }
            }

            // 取值范围
            double w = W_UPPERBOUND - Math.pow(((double) t) / MAX_ITERATION, 2) * (W_UPPERBOUND - W_LOWERBOUND);

            for (int i = 0; i < SWARM_SIZE; i++) {

                // 每个particle都是从swarm里面取出的，用来更新粒子群
                Particle p = swarm.get(i);

                // step 3 - update velocity
                double[][] newVel = new double[N][T];
                for (int j = 0; j < N; j++) {
                    for (int k = 0; k < T; k++) {
                        double r1 = generator.nextDouble();
                        double r2 = generator.nextDouble();
                        newVel[j][k] = (w * p.getVel().getVel()[j][k])
                                + (r1 * C1) * (pBestLocation.get(j).getLoc()[j][k] - p.getLoc().getLoc()[j][k])
                                + (r2 * C2) * (gBestLocation.getLoc()[j][k] - p.getLoc().getLoc()[j][k]);
                        if (newVel[j][k] > ProblemSet.VEL_HIGH) {
                            newVel[j][k] = ProblemSet.VEL_HIGH;
                        }
                        if (newVel[j][k] < ProblemSet.VEL_LOW) {
                            newVel[j][k] = ProblemSet.VEL_LOW;
                        }
                    }

                }
                Velocity vel = new Velocity(newVel);
                p.setVel(vel);

                // step 4 - update location
                int[][] newLoc = new int[N][T];
                for (int j = 0; j < N; j++) {
                    for (int k = 0; k < T; k++) {
                        double r3 = generator.nextDouble();
                        // sigmiod函数
                        if (r3 < 1 / (1 + Math.exp(-newVel[j][k]))) {
                            newLoc[j][k] = 1;
                        } else {
                            newLoc[j][k] = 0;
                        }
                    }
                }

				/*
				 * repair
				 */
                repair(newLoc, station, head, output, initialStatus);

                Location loc = new Location(newLoc);
                p.setLoc(loc);
            }


            System.out.println("ITERATION " + t + ": ");
            System.out.println(gBest);
//			for (int i = 0; i < gBestLocation.getLoc().length; i++) {
//				System.out.println("     Best " + i +": " + gBestLocation.getLoc()[i]);
//			}

            t++;
            // 计算更新后粒子群的适应度
            updateFitnessList(initialStatus, inflow, beginLevel, output, allEotableList, station);
        }


        System.out.println("\nIntervalUtils found at iteration " + (t - 1) + ", the solutions is:");
        System.out.println(gBest);

        System.out.println(ProblemSet.getStartCost(gBestLocation.getLoc(), initialStatus));
//		for (int i = 0; i < gBestLocation.getLoc().length; i++) {
//			System.out.println("     Best " + i +": " + gBestLocation.getLoc()[i]);
//		}

        return ProblemSet.evaluate(gBestLocation.getLoc(), initialStatus, inflow, beginLevel, output, allEotableList,
                station);
    }

    /**
     * 粒子群初始化
     *
     * @param N 机组台数
     * @param T 时段数
     */
    public void initializeSwarm(int N, int T, HydroStation station, double head, double[] dailyLoad,
                                int[] initialStatus) {
        for (int i = 0; i < SWARM_SIZE; i++) {
            Particle p = new Particle();

            // randomize location inside a space defined in Problem Set
            int[][] loc = new int[N][T];
            for (int j = 0; j < N; j++) {
                for (int k = 0; k < T; k++) {
                    // 0 or 1使用nextInt函数，产生0（inclusive）到Bound（exclusive）的伪随机数
                    loc[j][k] = generator.nextInt(2);
                }
            }

            /*
             * repair
             */
            repair(loc, station, head, dailyLoad, initialStatus);

            Location location = new Location(loc);


            // randomize velocity in the range defined in Problem Set
            double[][] vel = new double[N][T];
            for (int j = 0; j < N; j++) {
                for (int k = 0; k < T; k++) {
                    vel[j][k] = ProblemSet.VEL_LOW + generator.nextDouble() *
                            (ProblemSet.VEL_HIGH - ProblemSet.VEL_LOW);
                }
            }
            Velocity velocity = new Velocity(vel);

            p.setLoc(location);
            p.setVel(velocity);
            swarm.add(p);
        }
    }

    /**
     * 计算粒子的适应度
     *
     * @param initialStatus  前一天最后一个时段的开停机状态
     * @param inflow         入库
     * @param beginLevel     初水位
     * @param output         出力
     * @param allEotableList 最优负荷分配表
     * @param station        电站
     */
    public void updateFitnessList(int[] initialStatus, double[] inflow, double beginLevel, double[] output,
                                  List<List<Eotable>> allEotableList, HydroStation station) {
        for (int i = 0; i < SWARM_SIZE; i++) {
            fitnessValueList[i] = swarm.get(i).getFitness(initialStatus, inflow, beginLevel, output, allEotableList, station);
        }
    }

    /**
     * 约束修正：旋转备用-->最小开停机-->关闭多余机组（每一步所有时段完成之后再进行下一步）
     *
     * @param location      机组组合
     * @param station       电站
     * @param head          水头
     * @param dailyLoad     日负荷
     * @param initialStatus 前一天最后一个时段的开停机状态
     */
    public static void repair(int[][] location, HydroStation station, double head, double[] dailyLoad, int[]
            initialStatus) {


        final int N = location.length;
        final int T = location[0].length;

        final int[] MDT = ProblemSet.MDT;
        final int[] MUT = ProblemSet.MUT;
        final int[] UNITPRIORITY = ProblemSet.UNITPRIORITY;


        // 开停机持续时间
        int[][] duration = getDuration(location, initialStatus);

        // 根据机组优先顺序表，对不满足要求的时段增开机组
        // 机组预想出力与水头有关，水头一天内的变化不大，先用平均水头代替吧
        double[] Nmax = new double[station.getUnitCount()];
        for (int i = 0; i < Nmax.length; i++) {
            Nmax[i] = station.getUnitById(i + 1).getHeadExceptedOutputCurve().getExceptedOutputByHead(head);
        }

        // step 1: Spinning reserve constraints repairing
        for (int t = 0; t < T; t++) {

            double totalN = 0;
            for (int i = 0; i < N; i++) {
                totalN += Nmax[i] * location[i][t];
            }

            // 不满足要求，按照机组优先顺序逐次开机
            int[] ucT = MathUtils.getColumn(location, t);
            if (!isSatisfied(ucT, head, dailyLoad[t], totalN, station)) {
                for (int n = 0; n < N; n++) {
                    // 机组优先顺序是从1开始的
                    int index = ArrayUtils.indexOf(UNITPRIORITY, n + 1);
                    if (location[index][t] == 0) {
                        location[index][t] = 1;
                        totalN += Nmax[index];
                    }
                    if (isSatisfied(ucT, head, dailyLoad[t], totalN, station))
                        break;
                }
            }

            duration = getDuration(location, initialStatus);
        }

        // step 2: Minimum up and down time constraints repairing
        for (int t = 0; t < T; t++) {
            timeRepair(t, location, duration, MUT, MDT, initialStatus);
            duration = getDuration(location, initialStatus);
        }

        // step 3: Decommitment of excess units
        for (int t = 0; t < T; t++) {

            for (int n = N - 1; n >= 0; n--) {
                // 依次选择机组优先顺序最小，即满发耗水量最大的机组
                int index = ArrayUtils.indexOf(UNITPRIORITY, n + 1);
                if (location[index][t] == 1) {
                    // 判断该机组停机是否满足旋转备用约束
                    double sumN = 0;
                    for (int j = 0; j < N; j++) {
                        if (j == index)
                            continue;
                        sumN += Nmax[j] * location[j][t];
                    }

                    int[] uc = MathUtils.getColumn(location, t);
                    // 使index对应的那台机组停机
                    uc[index] = 0;

                    if (isSatisfied(uc, head, dailyLoad[t], sumN, station)) {
                        // 前时段满足开停机约束
                        if (duration[index][t] == 1 || duration[index][t] > MUT[index]) {
                            location[index][t] = 0;
//                            // 后时段满足旋转备用
//                            boolean flag = true;
//                            for (int k = 1; k < MDT[n] && (t + k) < T; k++) {
//                                int[] tempUC = MathUtils.getColumn(location, t + k);
//                                tempUC[index] = 0;
//                                double sumLoad = 0;
//                                for (int m = 0; m < tempUC.length; m++) {
//                                    sumLoad += station.getUnitById(m + 1).getHeadExceptedOutputCurve()
//                                            .getExceptedOutputByHead(head) * tempUC[m];
//                                }
//                                flag = flag && isSatisfied(tempUC, head, dailyLoad[t + k], sumLoad, station);
//                            }
//                            if (flag) {
//                                for (int k = 0; k < MDT[n] && (t + k) < T; k++) {
//                                    location[index][t + k] = 0;
//                                }
//                            }
                        }
                    }
                }
            }

            duration = getDuration(location, initialStatus);

        }

        // step 4: Minimum up and down time constraints repairing
        // 没必要的，但是考虑到特殊情况（0000011111[11111]1100000），中间的1是多余机组且初始解就是如此，去除之后会使后面两个时段
        // 不满足最小开机时间约束（MDT = MUT = 5）
        for (int t = 0; t < T; t++) {
            timeRepair(t, location, duration, MUT, MDT, initialStatus);
            duration = getDuration(location, initialStatus);
        }

    }


    /**
     * 根据机组组合得到开停机持续时间
     *
     * @param location      机组组合
     * @param initialStatus 前一天最后一个时段的开停机状态
     */
    public static int[][] getDuration(int[][] location, int[] initialStatus) {
        final int N = location.length;
        final int T = location[0].length;

        int[][] duration = new int[N][T];
        for (int n = 0; n < N; n++) {
            for (int t = 0; t < T; t++) {
                int previousStatus;
                if (t == 0) {
                    previousStatus = initialStatus[n];
                } else {
                    previousStatus = duration[n][t - 1];
                }
                // 开机状态
                if (location[n][t] == 1) {
                    if (previousStatus > 0) {
                        duration[n][t] = previousStatus + 1;
                    } else {
                        duration[n][t] = 1;
                    }
                } else {
                    if (previousStatus > 0) {
                        duration[n][t] = -1;
                    } else {
                        duration[n][t] = previousStatus - 1;
                    }
                }
            }
        }

        return duration;
    }

    /**
     * 最短开停机时间约束处理，为防止破坏旋转备用约束，只能开机
     *
     * @param t             时段
     * @param location      机组组合
     * @param duration      开停机持续时间
     * @param MUT           最短开机时间
     * @param MDT           最短停机时间
     * @param initialStatus 初始开停机状态
     */
    private static void timeRepair(int t, int[][] location, int[][] duration, int[] MUT, int[] MDT, int[]
            initialStatus) {

        final int N = location.length;
        final int T = location[0].length;

        for (int n = 0; n < N; n++) {

            int previous, previousStatus;
            if (t == 0) {
                previousStatus = initialStatus[n];
                previous = (previousStatus > 0) ? 1 : 0;
            } else {
                previousStatus = duration[n][t - 1];
                previous = location[n][t - 1];
            }

            // 由1-->0
            if (previous == 1 && location[n][t] == 0) {
                if (previousStatus < MUT[n]) {
                    location[n][t] = 1;
                }

                if (t + MDT[n] <= T && -duration[n][t + MDT[n] - 1] < MDT[n]) {
                    location[n][t] = 1;
                }

                if (t + MDT[n] > T) {
                    int sum = 0;
                    for (int i = t; i < T; i++) {
                        sum += location[n][i];
                    }
                    if (sum > 0) {
                        location[n][t] = 1;
                    }
                }
            }

        }
    }

    /**
     * 判断是否满足开大机组必须开一台小机组的约束
     *
     * @param uc 某时段的机组组合
     * @return OK？
     */
    private static boolean isOK(int[] uc) {
        if (uc[4] == 1 || uc[5] == 1) {
            int sum = 0;
            for (int i = 0; i < 4; i++) {
                sum += uc[i];
            }
            if (sum == 0)
                return false;
        }
        return true;
    }

    /**
     * 判断某一时段机组组合是否满足要求
     *
     * @param uc      机组组合
     * @param head    水头
     * @param load    负荷
     * @param totalN  最大出力
     * @param station 电站
     * @return true or false
     */
    private static boolean isSatisfied(int[] uc, double head, double load, double totalN, HydroStation station) {
        // 当全部停机的时候，可行区计算不出来，先做判断
        int[] allOff = {0, 0, 0, 0, 0, 0};
        if (Arrays.equals(uc, allOff)) {
            return false;
        }
        // 出力在可行区
        boolean isInFeasibleZone = IntervalUtils.contains(Math.floor(load),
                DPUC.getFeasibleZone(uc, head, station));
        // 必须开一台小机组
        boolean isOK = isOK(uc);

        return isInFeasibleZone && isOK && (totalN > load);
    }

}

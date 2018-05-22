package com.example.satellite.economicaloperation;

import com.example.satellite.domain.Eotable;
import com.example.satellite.station.HydroStation;
import com.example.satellite.station.unit.Unit;
import com.example.satellite.util.Interval;
import com.example.satellite.util.MathUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * DP算法计算固定机组之间的最优负荷分配
 * 给定一个出力和机组组合，得到该出力对应的负荷分配和耗流量，只需一条Eotable即可
 * 需处理小数：出力向上取整，得到最优分配之后，将最大出力减去一点
 * 在此之前已经经过了出力约束的判断
 *
 * @author : GXW
 */
public class EconomicDispatch {

    private static final double ERROR = 0.001;

	/**
	 * “以电定水”模块，求得给定出力下的最优流量及负荷分配
	 *
	 * @param station: 电站
     * @param uc: 机组组合
	 * @param head: 水头
	 * @param giveN: 给定出力（即为总的最大出力）
	 */
	public static Eotable dispatchUnitN(HydroStation station,int[] uc, double head, double giveN) {
	    // 开机机组
        List<Unit> unitList = new ArrayList<>();
        int smallUnitCount = 0;
	    for (int i = 0; i < uc.length; i++) {
	        if (uc[i] == 1) {
	            if (i < 4) {
	                smallUnitCount++;
                }
	           unitList.add(station.getUnitById(i + 1));
            }
        }
		// 机组台数
		int unitCount = unitList.size();

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
//        System.out.println("机组台数为" + unitCount);
//        System.out.println("水头为" + head);
//        System.out.println("最大出力： " + Arrays.toString(maxN));
//        System.out.println("最小出力： " + Arrays.toString(minN));

        // 步长：1MW
        double intervalN = 1.0;
        // 出力向上取整
        double sumN = Math.ceil(giveN);
//        System.out.println("总出力： " + giveN);


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
        double[] bestN = new double[unitCount];
        double[] bestQ = new double[unitCount];

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
                    else if (EconomicDispatchTable.isInPOZ(stateVariable[j], unitList.get(i).getPozList())) {
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

                        } else if (EconomicDispatchTable.isInPOZ(stateVariable[k], unitList.get(i).getPozList())) {
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
                        if (i >= smallUnitCount && k == j) {
                            tempQ = Q_PENALTY;
                        }

                        // 判断耗流量的大小，小的话就找到了较优解，赋值
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
		int lastIndex = stateVariable.length - 1;
        double leaveValue = stateVariable[lastIndex];

        for (int i = unitCount - 1; i >= 0; i--) {
            if (i == unitCount - 1) {
                bestN[i] = optimalN[i][lastIndex];
                bestQ[i] = unitList.get(i).getNhqCurve().getQByHN(head, bestN[i]);
            } else {
                leaveValue -= bestN[i + 1];
                // 出力为leaveValue对应的下标为leaveValue / intervalN取整
                bestN[i] = optimalN[i][(int) (leaveValue / intervalN)];
                bestQ[i] = unitList.get(i).getNhqCurve().getQByHN(head, bestN[i]);
            }
        }

        Eotable eotable = new Eotable();
        // bestNQ不是全部机组组合，要把停机的机组也加入数组里面
        double[] allBestN = new double[uc.length];
        double[] allBestQ = new double[uc.length];
        int index = 0;
        for (int i = 0; i < allBestN.length; i++) {
            if (uc[i] == 1) {
                allBestN[i] = bestN[index];
                allBestQ[i] = bestQ[index];
                index++;
            }
        }
        eotable.setN(allBestN);
        eotable.setSumN(MathUtils.getSum(allBestN));
        eotable.setQ(allBestQ);
        eotable.setSumQ(MathUtils.getSum(allBestQ));

        // 和不相等时，处理余数,sumN 是 giveN 向上取整的数
        if (Math.abs(sumN - giveN) > ERROR) {
            double[] n = eotable.findNArray();
            int maxIndex = MathUtils.getMaxIndex(n);
            n[maxIndex] -= (sumN - giveN);
            eotable.setN(n);
            eotable.setSumN(MathUtils.getSum(n));
            double[] q = eotable.findQArray();
            q[maxIndex] = station.getUnitById(maxIndex + 1).getNhqCurve().getQByHN(head, n[maxIndex]);
            eotable.setQ(q);
            eotable.setSumQ(MathUtils.getSum(q));
        }

        return eotable;

	}
//
//	/**
//	 * “以水定电”模块，求得给定流量下的最优出力及流量分配
//	 *
//	 * @param nhqCurve
//	 * @param head
//	 * @param maxN
//	 * @param minN
//	 * @param maxQ
//	 * @param minQ
//	 * @param giveQ
//	 * @return
//	 */
//	public static void dispatchUnitQ(NHQCurve[] nhqCurve, double head, double[] maxN, double[] minN, double[] maxQ,
//			double[] minQ, double giveQ) {
//
//		// 机组数
//		int unitCount = nhqCurve.length;
//
//		/*
//		 * 如果给定流量大于unitCount台机组的最大总流量，最优分配即为每台机组的最大流量，计算结束
//		 * 如果给定流量小于unitCount台机组的最小总流量，最优分配即为每台机组的最小流量，计算结束
//		 */
//		double sumMaxQ = 0;
//		double sumMinQ = 0;
//		for (int i = 0; i < unitCount; i++) {
//			sumMaxQ += maxQ[i];
//			sumMinQ += minQ[i];
//		}
//		if (giveQ >= sumMaxQ) {
//			double[] noBestQ = new double[unitCount];
//			for (int i = 0; i < unitCount; i++) {
//				noBestQ[i] = maxQ[i];
//			}
//			// return noBestQ;
//		} else if (sumMinQ >= giveQ) {
//			double[] noBestQ = new double[unitCount];
//			for (int i = 0; i < unitCount; i++) {
//				noBestQ[i] = minQ[i];
//			}
//			// return noBestQ;
//		}
//
//		// 步长
//		double intervalQ = 1;
//		// 处理小数
//		double leaveQ = giveQ % intervalQ;
//		// 新的总流量
//		double newGiveQ = giveQ - leaveQ;
//		// 不满足流量的出力惩罚值
//		final double N_PENALTY = -10000;
//		// 新的上下限，原来的存储在old数组中
//		double[] oldMinQ = new double[minQ.length];
//		double[] oldMaxQ = new double[maxQ.length];
//		for (int i = 0; i < minQ.length; i++) {
//			oldMinQ[i] = minQ[i];
//			oldMaxQ[i] = maxQ[i];
//			minQ[i] = oldMinQ[i] - oldMinQ[i] % intervalQ;
//			maxQ[i] = oldMaxQ[i] - oldMaxQ[i] % intervalQ + intervalQ;
//		}
//
//		/*
//		 * 生成状态变量（unitCount台机组的总流量）
//		 */
//		int size = (int) (newGiveQ / intervalQ) + 1;
//		double[] stateVariable = new double[size];
//		for (int i = 0; i < size; i++) {
//			stateVariable[i] = i * intervalQ;
//		}
//
//		/*
//		 * 动态规划顺向递推，求解计算
//		 */
//		// 返回的最优分配结果
//		double[][] bestQ = new double[size][unitCount];
//
//		// 中间存储结果
//		double[][] optimalQ = new double[unitCount][size];
//		double[][] optimalN = new double[unitCount][size];
//
//		// 阶段变量，机组台数unitCount
//		for (int i = 0; i < unitCount; i++) {
//			for (int j = 0; j < size; j++) {
//				// 第一台机组
//				if (i == 0) {
//					optimalQ[i][j] = stateVariable[j];
//					// 不在机组流量上下限的加入惩罚
//					if (stateVariable[j] < minQ[i] - 1E-2) {
//						optimalN[i][j] = -10000 * Math.abs(stateVariable[j] - minQ[i]);
//					} else if (stateVariable[j] > maxQ[i] + 1E-1) {
//						optimalN[i][j] = -10000 * Math.abs(stateVariable[j] - maxQ[i]);
//					} else {
//						optimalN[i][j] = nhqCurve[i].getNByHQ(head, stateVariable[j]);
//
//						if (optimalN[i][j] < minN[i] - 1E-1 || optimalN[i][j] > maxN[i] + 1E-1) {
//							optimalN[i][j] = -10000;
//						}
//					}
//					// 后几台机组，顺向递推
//				} else {
//					optimalN[i][j] = N_PENALTY;
//					for (int k = 0; k <= j; k++) {
//						// 前面i - 1台机组j - k的流量对应的出力
//						double tempN = optimalN[i - 1][j - k];
//						if (stateVariable[k] < minQ[i] - 1E-2) {
//							tempN += (-10000 * Math.abs(stateVariable[k] - minQ[i]));
//						} else if (stateVariable[k] > maxQ[i] + 1E-1) {
//							tempN += (-10000 * Math.abs(stateVariable[k] - maxQ[i]));
//						} else {
//							double tempNN = nhqCurve[i].getNByHQ(head, stateVariable[j]);
//							if (tempNN > maxN[i] + 1E-1 || tempNN < minN[i] - 1E-1) {
//								tempNN = -10000;
//							} else {
//								if (tempN == N_PENALTY) {
//									tempN = tempNN;
//								} else {
//									tempN += tempNN;
//								}
//							}
//						}
//
//						// 判断出力的大小，大的话就找到了较优解，赋值
//						if (tempN > optimalN[i][j]) {
//							optimalN[i][j] = tempN;
//							optimalQ[i][j] = stateVariable[k];
//						}
//					}
//				}
//			}
//		}
//
//		/*
//		 * 逆向递推，计算各状态变量下的最优流量
//		 */
//		for (int j = 0; j < size; j++) {
//			double leaveValue = stateVariable[j];
//			if (leaveValue == 0)
//				continue;
//
//			for (int i = unitCount - 1; i >= 0; i--) {
//				if (i == unitCount - 1)
//					bestQ[j][i] = optimalQ[i][j];
//				else {
//					leaveValue = leaveValue - bestQ[j][i + 1];
//					bestQ[j][i] = optimalQ[i][(int) (leaveValue / intervalQ)];
//				}
//			}
//		}
//		// 最优出力计算完毕
//
//		/*
//		 * 考虑最小值，使得流量不小于最小值
//		 */
//		for (int i = 0; i < bestQ.length; i++) {
//			for (int j = 0; j < bestQ[i].length; j++) {
//				if (Math.abs(minQ[j] - oldMinQ[j]) < 1E-1)
//					continue;
//
//				if (bestQ[i][j] < oldMinQ[j] - 1E-1) {
//					bestQ[i][j] = oldMinQ[j];
//				}
//			}
//		}
//
//		/*
//		 * 考虑最大值，使得流量不大于最大值
//		 */
//		for (int i = 0; i < bestQ.length; i++) {
//			for (int j = 0; j < bestQ[i].length; j++) {
//				if (Math.abs(maxN[j] - oldMaxQ[j]) < 1E-1)
//					continue;
//
//				if (bestQ[i][j] > oldMaxQ[j] + 1E-1) {
//					leaveQ += (bestQ[i][j] - oldMaxQ[j]);
//					bestQ[i][j] = oldMaxQ[j];
//				}
//			}
//		}
//
//		// 加上余数之后的最终值
//		double[] needBestQ = bestQ[size - 1];
//
//		/*
//		 * 把余数加上去
//		 */
//		for (int i = 0; i < needBestQ.length; i++) {
//			if (leaveQ < 1E-1)
//				break;
//
//			needBestQ[i] += leaveQ;
//
//			if (needBestQ[i] > oldMaxQ[i]) {
//				leaveQ = (needBestQ[i] - oldMaxQ[i]);
//				needBestQ[i] = oldMaxQ[i];
//				continue;
//			}
//
//			break;
//		}
//
//		/*
//		 * 求和
//		 */
//		double findSumQ = 0;
//		for (int i = 0; i < needBestQ.length; i++) {
//			findSumQ += needBestQ[i];
//		}
//
//		/*
//		 * 当和不相等时
//		 */
//		double moreQ = findSumQ - giveQ;
//		if (moreQ >= 1E-1) {
//			for (int i = 0; i < needBestQ.length; i++) {
//				if (moreQ < 1E-1)
//					break;
//
//				needBestQ[i] -= moreQ;
//
//				if (needBestQ[i] < oldMinQ[i]) {
//					moreQ = (oldMinQ[i] - needBestQ[i]);
//					needBestQ[i] = oldMinQ[i];
//					continue;
//				}
//
//				break;
//			}
//		} else {
//			moreQ = -moreQ;
//			for (int i = 0; i < needBestQ.length; i++) {
//				if (moreQ < 1E-1)
//					break;
//
//				needBestQ[i] += moreQ;
//
//				if (needBestQ[i] > oldMaxQ[i]) {
//					moreQ = (needBestQ[i] - oldMaxQ[i]);
//					needBestQ[i] = oldMaxQ[i];
//					continue;
//				}
//
//				break;
//			}
//
//		}
//
//	}

}

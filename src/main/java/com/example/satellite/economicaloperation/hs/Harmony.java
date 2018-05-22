package com.example.satellite.economicaloperation.hs;

import com.example.satellite.domain.Eotable;
import com.example.satellite.economicaloperation.Node;
import com.example.satellite.economicaloperation.pso.ProblemSet;
import com.example.satellite.station.HydroStation;
import com.example.satellite.util.MathUtils;

import java.util.Arrays;
import java.util.List;

/**
 * 音符类，相当于粒子群算法中的一个粒子
 * 
 * @author Dell Precision
 *
 */
public class Harmony {
	// 适应度
	private double fitnessValue;
	// 取值，对应于机组组合问题，就是时段数*机组台数的二维数组
	private int[][] location;

	public Harmony() {
		super();
	}

    public double getFitnessValue(int[] initialStatus, double[] inflow, double beginLevel, double[] output,
                             List<List<Eotable>> allEotableList, HydroStation station) {

        List<Node> nodeList = ProblemSet.evaluate(location, initialStatus, inflow, beginLevel, output,
                allEotableList,
                station);
        fitnessValue = 0;
        for (Node node : nodeList) {
            fitnessValue += node.getFitness();
        }

        fitnessValue += ProblemSet.getStartCost(this.location, initialStatus);

        return fitnessValue;
    }

	public int[][] getLocation() {
		return location;
	}

	public void setLocation(int[][] location) {
		this.location = location;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < location.length; i++) {
			sb.append(Arrays.toString(location[i]));
			sb.append("\n");
		}
		return sb.toString();
	}

    /**
     * 获取某个时段的机组组合
     * @param t 时段t，从0开始
     * @return uc
     */
    public int[] getUC(int t) {
        if (location == null) {
            throw new IllegalArgumentException("Location is null");
        }

        return MathUtils.getColumn(location, t);
    }
	
	
	
	
}

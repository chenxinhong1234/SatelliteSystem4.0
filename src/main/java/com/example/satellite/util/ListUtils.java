package com.example.satellite.util;

import java.util.List;

public class ListUtils {

    /**
     * Double的list转成double的数组
     * @param list
     * @return
     */
	public static double[] listToArray (List<Double> list) {
		Double[] data = list.toArray(new Double[list.size()]);
		double[] result = new double[data.length];
		for (int i = 0; i < result.length; i++) {
            result[i] = data[i];
        }
		return result;
	}
}

package com.example.satellite.util;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Stream;

/**
 * 
 * Description: 一些常用的数学计算方法
 *
 * @author: GXW
 * @date:   2016年4月13日
 */
public class MathUtils {

	/**
	 * 求和
	 *
	 * @param array
	 * @return
	 */
	public static double getSum(double[] array) {
		double sum = 0;
		for (double x : array) {
			sum += x;
		}
		return sum;
	}

	/**
	 * 前几个数求和，从0开始，到endIndex - 1的下标
	 *
	 * @param array
	 * @return
	 */
	public static double getSum(double[] array, int endIndex) {
		double sum = 0;
		if (endIndex < 0 || endIndex > array.length - 1) {
			throw new IllegalArgumentException("Wrong endIndex!");
		}
		for (int i = 0; i < endIndex; i++) {
			sum += array[i];
		}
		return sum;
	}


	/**
	 * 求最大值
	 * 
	 * @param array
	 * @return
	 */
	public static double getMax(double[] array) {
		double max = array[0];
		for (double x : array) {
			if (max < x) {
				max = x;
			}
		}
		return max;
	}

	/**
	 * 求最小值对应的下标
	 *
	 * @param array
	 * @return
	 */
	public static int getMaxIndex(double[] array) {
		double max = array[0];
		int maxIndex = 0;
		for (int i = 1; i < array.length; i++) {
			if (max < array[i]) {
				max = array[i];
				maxIndex = i;
			}
		}
		return maxIndex;
	}

	/**
	 * 求最小值
	 * 
	 * @param array
	 * @return
	 */
	public static double getMin(double[] array) {
		double min = array[0];
		for (double x : array) {
			if (min > x) {
				min = x;
			}
		}
		return min;
	}

	/**
	 * 求最小值对应的下标
	 * 
	 * @param array
	 * @return
	 */
	public static int getMinIndex(double[] array) {
		double min = array[0];
		int minIndex = 0;
		for (int i = 1; i < array.length; i++) {
			if (min > array[i]) {
				min = array[i];
				minIndex = i;
			}
		}
		return minIndex;
	}

    public static int getMinIndex(List<Double> doubleList) {
	    double min = doubleList.get(0);
        int minIndex = 0;
        for (int i = 1; i < doubleList.size(); i++) {
            if (min > doubleList.get(i)) {
                min = doubleList.get(i);
                minIndex = i;
            }
        }
        return minIndex;
    }

	/**
	 * 随机生成和为1的数组
	 * 
	 * @param number
	 * @return
	 */
	public static double[] random(int number) {
		Random rand = new Random();
		double[] result = new double[number];
		double Sum = 0;
		for (int i = 0; i < result.length; i++) {
			result[i] = rand.nextDouble();
			Sum += result[i];
		}
		for (int i = 0; i < result.length; i++) {
			result[i] /= Sum;
		}
		return result;
	}

	/**
	 * 二分查找key对应的下标，当数组中不包括key时取离key最近且小于key的数的下标 key小于最小值返回-1 key大于最大值返回-2
	 * 但当key等于最大值时返回的是前一个数的下标
	 * 
	 * @param key
	 * @param sortedArray
	 * @return
	 */
	
	// 最好加一个符号标记，判断是顺序还是逆序
	public static int binaryLocation(double key, double[] sortedArray) {
		int low = 0;
		int high = sortedArray.length - 1;
		
		// 判断是顺序还是逆序
		double order = 1.0;
		if (sortedArray[low] > sortedArray[high])
			order = -1.0;		

		// 如果key小于最小值(大于最大值)，返回-1
		if (order * key < order * sortedArray[low])
			return -1;
		// 如果key大于最大值(小于最小值)，返回-2
		if (order * key > order * sortedArray[high])
			return -2;

		while (high != low + 1) {
			int mid = (high + low) / 2;
			if (order * key == order * sortedArray[mid])
				return mid;
			else if (order * key < order * sortedArray[mid])
				high = mid;
			else
				low = mid;
		}
		return low;
	}

	/**
	 * 线性插值返回x对应的y值
	 * 
	 * @param x
	 * @param ascXArray
	 * @param sortedYArray
	 * @return
	 */
	public static double binarySearch(double x, double[] ascXArray, double[] sortedYArray) {
		// 查找x对应的下标
		int location = binaryLocation(x, ascXArray);
		if (location == -1)
			return sortedYArray[0];
		if (location == -2)
			return sortedYArray[sortedYArray.length - 1];

		// 两点线性插值
		return sortedYArray[location] + (sortedYArray[location + 1] - sortedYArray[location])
				/ (ascXArray[location + 1] - ascXArray[location]) * (x - ascXArray[location]);
	}

    /**
     * 将min--max按照给定精度离散
     * @param min
     * @param max
     * @param precision
     * @return
     */
	public static double[] discreteToArray (double min, double max, double precision) {
        if (min > max) {
            throw new IllegalArgumentException("min must be less than or equal to max!");
        }
        int count = (int) ((max - min) / precision) + 1;
        double[] result  = new double[count];
        for (int i = 0; i < count; i++) {
            result[i] = DoubleUtils.add(min, DoubleUtils.mul(precision, (double) i));
        }
        return result;
    }

	/**
	 * 按照给定因子取最为接近的倍数
	 * @param value
	 * @param factor
     * @return
     */
	public static double mRound(double value, double factor) {
		return Math.round(value / factor) * factor;
	}


	/**
	 * 取最接近的下限值，floor，地板
	 * @param value
	 * @param factor
	 * @return
	 */
	public static double mFloor(double value, double factor) {
		return Math.floor(value / factor) * factor;
	}

	/**
	 * 取最接近的上限值，ceil，天花板
	 * @param value
	 * @param factor
	 * @return
	 */
	public static double mCeil(double value, double factor) {
		return Math.ceil(value / factor) * factor;
	}

    /**
     * 获取二维数组的一列，从0开始
     * @param source
     * @param columnIndex
     * @return
     */
	public static int[] getColumn(int[][] source, int columnIndex) {
	    int[] result = new int[source.length];
        for (int i = 0; i < source.length; i++) {
            result[i] = source[i][columnIndex];
        }
        return result;
    }

	/**
	 * 获取平均值
	 * @param source
	 * @return
	 */
	public static double getAverage(double[] source) {
    	double sum = 0;
		for (double d : source) {
			sum += d;
		}
		return sum / source.length;
	}

	/**
	 * Map sorted by value
	 * @param map map
	 * @return sorted map
	 */
	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
		Map<K, V> result = new LinkedHashMap<>();
		Stream<Map.Entry<K, V>> st = map.entrySet().stream();

		st.sorted(Map.Entry.comparingByValue()).forEachOrdered(e -> result.put(e.getKey(), e.getValue()));

		return result;
	}

}

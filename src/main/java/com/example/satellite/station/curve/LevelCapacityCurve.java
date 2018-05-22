package com.example.satellite.station.curve;

import com.example.satellite.domain.ZvEntity;
import com.example.satellite.util.Interpolator;

import java.util.ArrayList;
import java.util.List;

/**
 * 水位--库容关系曲线类
 * 
 * @author: GXW
 * @date: 2016年3月4日
 */
public class LevelCapacityCurve {
     /*这里定义的level和capacity是用来处理插值的，不是用来显示水位库容表格和曲线的*/

     // 存放水位数据
	private List<Double> level= new ArrayList<>();
	// 存放库容数据
	private List<Double> capacity = new ArrayList<>();

	/* 数组：用于存放水位和库容数据*/
	private double[][] data;

    // 插值类
	private Interpolator interpolator;

	public List<Double> getLevel() {
		return level;
	}

	public List<Double> getCapacity() {
		return capacity;
	}

	public double[][] getData() {
		return data;
	}
	public LevelCapacityCurve() {

	}

  /*从实体类中获得数据库中的水位、库容数据*/
	public LevelCapacityCurve(List<ZvEntity> zvs) {
		int index = 0;
		data = new double[zvs.size()][2];
		for (ZvEntity zv : zvs) {
			data[index][0] = zv.getZ();
			data[index][1] = zv.getV();
			level.add(zv.getZ());
			capacity.add(zv.getV());
			index++;
		}
		interpolator = new Interpolator(level, capacity);
	}

	/**
	 * 线性插值求水位
	 * 
	 * @param c
	 * @return
	 */
	public double getLevelByCapacity(double c) {
		return interpolator.interpolateXByY(c);
	}

	/**
	 * 线性插值求库容
	 * 
	 * @param l
	 * @return
	 */
	public double getCapacityByLevel(double l) {
		return interpolator.interpolateYByX(l);
	}

	/**
	 * 插值获取水位由l1 --> l2的库容减少量
	 * @param l1 ：初水位
	 * @param l2 ：末水位
     * @return
     */
	public double getDeltaCapacity(double l1, double l2) {
		return interpolator.interpolateYByX(l1) - interpolator.interpolateYByX(l2);
	}

}

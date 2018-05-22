package com.example.satellite.station.curve;

import com.example.satellite.domain.ZqEntity;
import com.example.satellite.util.Interpolator;

import java.util.ArrayList;
import java.util.List;

/**
 * 尾水位--下泄流量关系曲线类
 * 
 * @author: GXW
 * @date: 2016年3月4日
 */
public class DownlevelDischargeCurve {

	// 存放尾水位数据
	private List<Double> downlevel = new ArrayList<>();
	// 存放下泄流量数据
	private List<Double> discharge = new ArrayList<>();

	private double[][] data;
	// 插值类
	private Interpolator interpolator;

	public List<Double> getDischarge() {
		return discharge;
	}

	public List<Double> getDownlevel() {
		return downlevel;
	}

	public DownlevelDischargeCurve() {

	}

	public DownlevelDischargeCurve(List<ZqEntity> zqs) {
	    int index = 0;
	    data = new double[zqs.size()][2];
		for (ZqEntity zq : zqs) {
		    data[index][0] = zq.getZ();
		    data[index][1] = zq.getQ();
            downlevel.add(zq.getZ());
            discharge.add(zq.getQ());
            index++;
		}
		interpolator = new Interpolator(downlevel, discharge);
	}

	/**
	 * 线性插值求尾水位
	 * 
	 * @param d
	 * @return
	 */
	public double getDownlevelByDischarge(double d) {
		return interpolator.interpolateXByY(d);
	}

	/**
	 * 线性插值求下泄流量
	 * 
	 * @param dl
	 * @return
	 */
	public double getDischargeByDownlevel(double dl) {
		return interpolator.interpolateYByX(dl);
	}

	public double[][] getData() {
		return data;
	}

}

package com.example.satellite.station.unit.curve;

import com.example.satellite.domain.HeoEntity;
import com.example.satellite.util.Interpolator;

import java.util.ArrayList;
import java.util.List;

/**
 * 水头--预想出力曲线类
 * 
 * @author : GXW
 */
public class HeadExceptedOutputCurve {

	// 存放尾水位数据
	private List<Double> head = new ArrayList<>();
	// 存放预想出力数据
	private List<Double> exceptedOutput = new ArrayList<>();
	//存放水位、预想出力数据系列
	private double[][] data;
	// 插值类
	private Interpolator interpolator;
	// 预想出力达到最大值的水头位置
	private int headIndex = -1;

	public List<Double> getHead() {
		return head;
	}

	public List<Double> getExceptedOutput() {
		return exceptedOutput;
	}

	public HeadExceptedOutputCurve() {

	}

	/**
	 * 预想出力曲线不能直接使用插值类，因为后面对头对应的出力都相等
	 * 而且，只需要由水头查找预想出力
	 * @param eos
	 */
	public HeadExceptedOutputCurve(List<HeoEntity> eos) {

		data = new double[eos.size()][2];

		for (int i = 0; i < eos.size(); i++) {
			head.add(eos.get(i).getH());
			exceptedOutput.add(eos.get(i).geteOutput());
			data[i][0] = eos.get(i).getH();
			data[i][1] = eos.get(i).geteOutput();
		}

		for (int i = 0; i < eos.size() - 1; i++) {
			if (eos.get(i).geteOutput() == eos.get(i + 1).geteOutput()) {
				headIndex = i;
				break;
			}
		}

		// length = index + 1
		int length = (headIndex == -1) ? eos.size() : headIndex + 1;

		interpolator = new Interpolator(head.subList(0, length), exceptedOutput.subList(0, length));
	}

	/**
	 * 线性插值求预想出力
	 *
	 * @param h
	 * @return
	 */
	public double getExceptedOutputByHead(double h) {
		if (headIndex != -1 && h > head.get(headIndex)) {
			return exceptedOutput.get(headIndex);
		}

		return interpolator.interpolateYByX(h);
	}
	public double[][] getData() {
		return data;
	}
}

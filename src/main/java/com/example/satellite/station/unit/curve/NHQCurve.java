package com.example.satellite.station.unit.curve;

import com.example.satellite.domain.NhqEntity;
import com.example.satellite.util.Interpolator;
import com.example.satellite.util.ListUtils;
import com.example.satellite.util.MathUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * N--H--Q曲线类
 * 
 * @author: GXW
 * @date: 2016年3月4日
 */
public class NHQCurve {
    // 水头List
    private List<Double> H = new ArrayList<>();

    // 每一个水头下的NQ曲线
	private List<List<Double>> N = new ArrayList<>();
	private List<List<Double>> Q = new ArrayList<>();

	// 每一个H对应一个插值类
	private Interpolator[] interpolators;


	public NHQCurve() {
	}

	public NHQCurve(List<NhqEntity> nhqList) {
		// 水头Map，key = 水头，value = 相同水头的个数
		Map<Double, Integer> headMap = new TreeMap<>();
		for (NhqEntity nhq : nhqList) {
			Integer freq = headMap.get(nhq.getH());
			headMap.put(nhq.getH(), freq == null ? 1 : freq + 1);
		}

		// 添加数据
		int index = 0;
		for (Map.Entry<Double, Integer> entry : headMap.entrySet()) {
			List<Double> nList = new ArrayList<>();
			List<Double> qList = new ArrayList<>();
			for (int i = 0; i < entry.getValue(); i++) {
				nList.add(nhqList.get(index).getN());
				qList.add(nhqList.get(index).getQ());
				index++;
			}
			N.add(nList);
			Q.add(qList);
			H.add(entry.getKey());
		}

		// 插值类
		interpolators = new Interpolator[headMap.size()];
		for (int i = 0; i < H.size(); i++) {
			interpolators[i] = new Interpolator(N.get(i), Q.get(i));
		}
	}

	/**
	 * NHQ曲线差值求流量。 首先根据h插值求出对应水头下的N和H曲线，再由N-->Q
	 * 
	 * @param n
	 * @param h
	 * @return
	 */
	public double getQByHN(double h, double n) {

		if (h < H.get(0))
			h = H.get(0);
		if (h > H.get(H.size() - 1))
			h = H.get(H.size() - 1);

		int location = MathUtils.binaryLocation(h, ListUtils.listToArray(H));

		double tempQ1 = interpolators[location].interpolateYByX(n);
		double tempQ2 = interpolators[location + 1].interpolateYByX(n);

		return (h - H.get(location)) / (H.get(location + 1) - H.get(location)) * (tempQ2 - tempQ1) + tempQ1;
	}

	/**
	 * NHQ曲线差值求出力 首先根据h插值求出对应水头下的N和H曲线，再由Q-->N
	 * 
	 * @param h
	 * @param q
	 * @return
	 */
	public double getNByHQ(double h, double q) {

		if (h < H.get(0))
			h = H.get(0);
		if (h > H.get(H.size() - 1))
			h = H.get(H.size() - 1);

		int location = MathUtils.binaryLocation(h, ListUtils.listToArray(H));

		double tempN1 = interpolators[location].interpolateXByY(q);
		double tempN2 = interpolators[location + 1].interpolateXByY(q);

		return (h - H.get(location)) / (H.get(location + 1) - H.get(location)) * (tempN2 - tempN1) + tempN1;
	}

}

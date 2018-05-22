package com.example.satellite.util;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator;

import java.util.List;

/**
 * 
 * Description: 插值类，采用Apache-Commons-Math包进行插值 注意：数据必须升序排列，且不能在曲线外插值！
 * LinearInterpolator代表线性插值，SplineInterpolator代表曲线插值，采用后者
 * @author: GXW
 * @date:   2016年4月13日
 */
public class Interpolator {

	private UnivariateFunction fx;
	private UnivariateFunction fy;

	public Interpolator() {
	}

	public Interpolator(List<Double> X, List<Double> Y) {
		// 由x得到y
		UnivariateInterpolator uiy = new LinearInterpolator();
		fy = uiy.interpolate(ListUtils.listToArray(X), ListUtils.listToArray(Y));
		
		// 由y得到x
		UnivariateInterpolator uix = new LinearInterpolator();
		fx = uix.interpolate(ListUtils.listToArray(Y), ListUtils.listToArray(X));
	}

	public Interpolator(double[] X, double[] Y) {
		// 由x得到y
		UnivariateInterpolator uiy = new LinearInterpolator();
		fy = uiy.interpolate(X, Y);

		// 由y得到x
		UnivariateInterpolator uix = new LinearInterpolator();
		fx = uix.interpolate(Y, X);
	}

	/**
	 * 由x求y
	 * @param x
	 * @return
	 */
	public double interpolateYByX(double x) {
		return fy.value(x);
	}

	/**
	 * 由y求x
	 * @param y
	 * @return
	 */
	public double interpolateXByY(double y) {
		return fx.value(y);
	}

}

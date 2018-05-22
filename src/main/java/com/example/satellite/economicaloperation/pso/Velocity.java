package com.example.satellite.economicaloperation.pso;

/**
 * 速度向量，存储粒子飞行的速度，n维-->数组长度n
 * 
 * @author: GXW
 * @date:   2016年3月10日
 */
public class Velocity {

	// Location为二维数组，Velocity也为二维数组（N*T）
	private double[][] vel;

	public Velocity(double[][] vel) {
		this.vel = vel;
	}

	public double[][] getVel() {
		return vel;
	}

	public void setVel(double[][] vel) {
		this.vel = vel;
	}
}

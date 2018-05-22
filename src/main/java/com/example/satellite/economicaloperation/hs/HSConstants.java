package com.example.satellite.economicaloperation.hs;

public interface HSConstants {

	/**
	 * 算法的一些基本参数就暂时定义在这里吧
	 */
    // Harmony memory size
    int HMS = 30;
    // Number of new harmonies
    int nNew = 20;
    // Harmony memory considering rate
    double HMCR = 0.9;
    // Pitch adjusting rate
    double PAR = 0.1;
    // Band Width Damp Ratio
    double BW_damp = 0.995;
	
	// 最大迭代次数
	int MAX_ITERATION = 200;
	
	
}

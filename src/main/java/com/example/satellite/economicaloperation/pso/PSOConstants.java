package com.example.satellite.economicaloperation.pso;

/**
 * Description:
 * Created by Gaoxinwen on 2016/9/28.
 */
public interface PSOConstants {
    // 粒子群的数目
    int SWARM_SIZE = 30;
    // 最大迭代次数
    int MAX_ITERATION = 200;

    double C1 = 2.0;
    double C2 = 2.0;

    double W_UPPERBOUND = 1.0;
    double W_LOWERBOUND = 0.0;
}

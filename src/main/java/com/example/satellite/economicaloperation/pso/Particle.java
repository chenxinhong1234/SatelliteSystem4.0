package com.example.satellite.economicaloperation.pso;

import com.example.satellite.domain.Eotable;
import com.example.satellite.economicaloperation.Node;
import com.example.satellite.station.HydroStation;

import java.util.List;

/**
 * 粒子类，包含粒子的速度，位置和适应度
 * 
 * @author: GXW
 * @date: 2016年3月10日
 */
public class Particle {
	// 速度
	private Velocity vel;
	// 位置
	private Location loc;
	// 适应度
	private double fitness;

    public Velocity getVel() {
        return vel;
    }

    public void setVel(Velocity vel) {
        this.vel = vel;
    }

    public Location getLoc() {
        return loc;
    }

    public void setLoc(Location loc) {
        this.loc = loc;
    }

    public double getFitness(int[] initialStatus, double[] inflow, double beginLevel, double[] output,
                             List<List<Eotable>> allEotableList, HydroStation station) {

        List<Node> nodeList = ProblemSet.evaluate(this.loc.getLoc(), initialStatus, inflow, beginLevel, output, allEotableList,
                station);
        fitness = 0;
        for (Node node : nodeList) {
            fitness += node.getFitness();
        }

        fitness += ProblemSet.getStartCost(this.loc.getLoc(), initialStatus);

        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }
}

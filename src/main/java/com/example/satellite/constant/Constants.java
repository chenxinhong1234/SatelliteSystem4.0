package com.example.satellite.constant;

/**
 * Description:一些常量
 * Created by Gaoxinwen on 2016/8/18.
 */
public class Constants {

    private Constants() {
        throw new AssertionError();
    }

    /**
     * 电站名（电站编号），梯级电站在这里定义
     */
    public enum StationName {

        ZHELIN(1);         /*StationName(int id)  电站名（电站编号）*/

        private final int id;

        StationName(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }
}

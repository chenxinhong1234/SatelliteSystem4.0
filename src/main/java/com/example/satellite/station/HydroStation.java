package com.example.satellite.station;

import com.example.satellite.constant.Constants.StationName;
import com.example.satellite.domain.StationinfoEntity;
import com.example.satellite.station.curve.*;
import com.example.satellite.station.unit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 水电站类
 *
 * @author : GXW
 */
public class HydroStation {

    private static Logger logger = LoggerFactory.getLogger(HydroStation.class);

    // 电站编号
    private Integer id;
    // 电站名称
    private StationName name;
    // 电站基本信息
    private StationinfoEntity stationinfo;

    // 水位库容曲线   用于页面《水位库容曲线查询》中
    private LevelCapacityCurve levelCapacityCurve;
    // 尾水位下泄流量曲线
    private DownlevelDischargeCurve downlevelDischargeCurve;
    // 机组编号
    private Integer unitCount;
    // 机组list
    private List<Unit> unitList = new ArrayList<>();

   /* // 空构造类
    public HydroStation() {
    }*/


   /* public HydroStation(StationName stationName, StationinfoEntity stationinfo, LevelCapacityCurve levelCapacityCurve, DownlevelDischargeCurve downlevelDischargeCurve, Integer unitCount, List<Unit> unitList) {
        this.id = stationName.getId();
        this.name = stationName;
        this.stationinfo = stationinfo;
        this.levelCapacityCurve = levelCapacityCurve;
        this.downlevelDischargeCurve = downlevelDischargeCurve;

        this.unitCount = unitCount;
        this.unitList = unitList;

        logger.info("Initialize Station " + id + " Succeed!");
    }*/

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public StationName getName() {
        return name;
    }

    public void setName(StationName name) {
        this.name = name;
    }

    public StationinfoEntity getStationinfo() {
        return stationinfo;
    }

    public void setStationinfo(StationinfoEntity stationinfo) {
        this.stationinfo = stationinfo;
    }


    public LevelCapacityCurve getLevelCapacityCurve() {
        return levelCapacityCurve;
    }

    public void setLevelCapacityCurve(LevelCapacityCurve levelCapacityCurve) {
        this.levelCapacityCurve = levelCapacityCurve;
    }

    public DownlevelDischargeCurve getDownlevelDischargeCurve() {
        return downlevelDischargeCurve;
    }

    public void setDownlevelDischargeCurve(DownlevelDischargeCurve downlevelDischargeCurve) {
        this.downlevelDischargeCurve = downlevelDischargeCurve;
    }

    public Integer getUnitCount() {
        return unitCount;
    }

    public void setUnitCount(Integer unitCount) {
        this.unitCount = unitCount;
    }

    public List<Unit> getUnitList() {
        return unitList;
    }

    public void setUnitList(List<Unit> unitList) {
        this.unitList = unitList;
    }



    /**
     * 通过机组的id找到对应的机组，机组编号依次从1至unitCount
     *
     * @param unitId unitId
     * @return Unit
     */
    public Unit getUnitById(Integer unitId) {
        if (unitId <= 0 || unitId > this.unitCount) {
            throw new IllegalArgumentException("Can not find a unit whose id equals" + unitId);

        }
        // 电站编号是从1开始的，而List对应的index是从0开始的
//        if(unitId>4){
//            System.out.println("选择的机组类型为：类型2" );
//        }
//       else{
//            System.out.println("选择的机组类型为：类型1" );
//        }

        return unitList.get(unitId - 1);


    }


    /**
     * 得到给定水头下的总的预想出力
     *
     * @param head 水头
     * @return 总预想出力
     */
    public double getTotalExpectedOutput(double head) {
        double totalExpectedOutput = 0;
        for (Unit unit : unitList) {
            totalExpectedOutput += unit.getHeadExceptedOutputCurve().getExceptedOutputByHead(head);
        }

        return totalExpectedOutput;
    }
}

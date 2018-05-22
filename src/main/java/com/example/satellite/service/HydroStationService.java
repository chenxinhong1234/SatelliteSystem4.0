package com.example.satellite.service;

import com.example.satellite.constant.Constants.StationName;
import com.example.satellite.domain.Poz;
import com.example.satellite.domain.UnitgroupEntity;
import com.example.satellite.repository.*;
import com.example.satellite.station.HydroStation;
import com.example.satellite.station.curve.DownlevelDischargeCurve;
import com.example.satellite.station.curve.LevelCapacityCurve;
import com.example.satellite.station.unit.Unit;
import com.example.satellite.station.unit.curve.HeadExceptedOutputCurve;
import com.example.satellite.station.unit.curve.NHQCurve;
import com.example.satellite.util.Interval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Description: 获取电站
 * Created by Gaoxinwen on 2016/8/19.
 */
@Service
public class HydroStationService {

    @Autowired
    private StationinfoRepository stationinfoRepository;

    @Autowired
    private ZvRepository zvRepository;  /*从ZvRepository类获取数据库中的实体数据*/

    @Autowired
    private ZqRepository zqRepository;

    @Autowired
    private UnitgroupRepository unitgroupRepository;

    @Autowired
    private HeoRepository heoRepository;

    @Autowired
    private NhqRepository nhqRepository;

    @Autowired
    private PozRepository pozRepository;

    @Bean("abc")        /*生成 zhelin 的 Bean ，为zhelin做初始化，为其附上各种属性*/
    public HydroStation creatstation(){  /*获取station的属性，返回值station*/
        HydroStation station = new HydroStation();
        Integer stationId = StationName.ZHELIN.getId();  /* 这里得到了stationId=1，为下面的与数据库的链接接口提供钥匙*/
        station.setId(stationId);
        station.setName(StationName.ZHELIN);
        station.setStationinfo(stationinfoRepository.findOne(stationId));
        station.setLevelCapacityCurve(new LevelCapacityCurve(zvRepository.findByStationId(stationId)));
        station.setDownlevelDischargeCurve( new DownlevelDischargeCurve(zqRepository.findByStationId(stationId)));

        // 获取该电站的机组group与机组编号对应表
        List<UnitgroupEntity> unitgroupEntityList = unitgroupRepository.findByStationId(stationId);

        station.setUnitCount(unitgroupEntityList.size());

        // 相同机组类型的机组台数
        Map<Integer, Integer> unitCountMap = new TreeMap<>();
        for (UnitgroupEntity group : unitgroupEntityList) {
            Integer freq = unitCountMap.get(group.getGroupId());
            unitCountMap.put(group.getGroupId(), freq == null ? 1 : freq + 1);
        }

        int index = 0;
        List<Unit> unitList= new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : unitCountMap.entrySet()) {
            // 添加一个机组group下的unitList
            NHQCurve nhqCurve = new NHQCurve(nhqRepository.findByStationIdAndGroupId(stationId, entry.getKey()));
            HeadExceptedOutputCurve headExceptedOutputCurve = new HeadExceptedOutputCurve(
                    heoRepository.findByStationIdAndGroupId(stationId, entry.getKey()));
            for (int i = 0; i < entry.getValue(); i++) {
                int unitId = unitgroupEntityList.get(index).getUnitId();
                List<Interval> pozList = new ArrayList<>();
                List<Poz> pozs = pozRepository.findByStationIdAndUnitId(stationId, unitId);

                // 将Poz转化为Interval
                pozList.addAll(pozs.stream().map(poz ->
                        new Interval(poz.getLower(), poz.getUpper())).collect(Collectors.toList()));

                unitList.add(new Unit(unitId, entry.getKey(), stationId, nhqCurve,
                        headExceptedOutputCurve, pozList));
                index++;
            }
        }

        station.setUnitList(unitList);

        return station;

    }
}

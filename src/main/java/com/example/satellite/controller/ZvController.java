package com.example.satellite.controller;

import com.example.satellite.economicaloperation.Node;
import com.example.satellite.service.*;
import com.example.satellite.station.HydroStation;
import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Description:
 * Created by Gaoxinwen on 2016/12/26.
 */
@RestController
@RequestMapping("/data")
public class ZvController {

    @Autowired
    private HydroStation zhelin;

    /*通过@Autowired来获取“zhelin”这个bean，这里面的zhelin是个对象，不是类，是个实体，里面需要有各种属性，
    zhelin的Bean在HydroStationService类中生成*/

    @Autowired
    private TestService testService;

    @RequestMapping("/zv")
    public double[][] zv() {
        return zhelin.getLevelCapacityCurve().getData();
    }

    @RequestMapping("/zq")
    public double[][] zq() {
        double temp1[][] = zhelin.getDownlevelDischargeCurve().getData();
        //交换水位数据与下泄流量数据的位置
        double[][] temp2= new double[temp1.length][2];
         for(int i =0;i<temp1.length;i++) {
             temp2[i][0]=temp1[i][1];
             temp2[i][1]=temp1[i][0];
         }
        return  temp2;
    }
    @RequestMapping("/heo")
    public double[][] heo(@RequestBody SearchCriteria criteria) {

        if (StringUtils.equals(criteria.getPattern(), "unit1")){

            return zhelin.getUnitById(1).getHeadExceptedOutputCurve().getData();
        }
        else {
            return zhelin.getUnitById(5).getHeadExceptedOutputCurve().getData();
        }

    }

    @RequestMapping("/inter")
    public double zvInterpolate(@RequestBody SearchCriteria criteria) {
        double result;
        if (StringUtils.equals(criteria.getPattern(), "zv")) {
            result = zhelin.getLevelCapacityCurve().getCapacityByLevel(criteria.getValue());
        } else {
            result = zhelin.getLevelCapacityCurve().getLevelByCapacity(criteria.getValue());
        }

        return result;
    }

    @RequestMapping("/searchZq")
    public double zqInterpolate(@RequestBody SearchCriteria criteria) {
        double result;
        if (StringUtils.equals(criteria.getPattern(), "zq")) {
            result = zhelin.getDownlevelDischargeCurve().getDischargeByDownlevel(criteria.getValue());
        } else {
            result = zhelin.getDownlevelDischargeCurve().getDownlevelByDischarge(criteria.getValue());
        }

        return result;
    }

    @RequestMapping("/searchHeo")
    public double heoInterpolate(@RequestBody SearchCriteria criteria) {
        double result;
        int id;//机组id
        if (StringUtils.equals(criteria.getPattern(), "unit1")) {
            id = 1;//0-3,第一种类型机组
        }
        else {id = 5;//4-5第二种类型机组
        }


        result = zhelin.getUnitById(id).getHeadExceptedOutputCurve().getExceptedOutputByHead(criteria.getValue());

        return result;
    }


    @PostMapping("/economic_operation")
    @ResponseBody
    public List<Node> output(@RequestBody InputCriteria input) {
        return testService.test(input);
    }

}

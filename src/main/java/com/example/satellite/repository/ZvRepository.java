package com.example.satellite.repository;

import com.example.satellite.domain.ZvEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Description:
 * Created by Gaoxinwen on 2016/5/3.
 */
public interface ZvRepository extends JpaRepository<ZvEntity, Integer>{
    List<ZvEntity> findByStationId(Integer stationId);
    /*findByStationId的方法就相当于一条SQL语句，Spring Data JPA给你封装好了，不用自己写，只定义接口就行
    * 这里的接口是stationId的值，这个值的获取，在 HydroStationService 类 createStation()方法中，
    * 获取了 stationId=1 的值*/
}

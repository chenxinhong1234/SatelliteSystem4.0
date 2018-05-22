package com.example.satellite.repository;

import com.example.satellite.domain.UnitgroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Description:
 * Created by Gaoxinwen on 2016/5/3.
 */
public interface UnitgroupRepository extends JpaRepository<UnitgroupEntity, Integer> {
    List<UnitgroupEntity> findByStationId(Integer stationId);
}

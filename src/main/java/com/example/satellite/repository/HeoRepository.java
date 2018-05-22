package com.example.satellite.repository;

import com.example.satellite.domain.HeoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Description:
 * Created by Gaoxinwen on 2016/5/3.
 */
public interface HeoRepository extends JpaRepository<HeoEntity, Integer> {
    List<HeoEntity> findByStationIdAndGroupId(Integer stationId, Integer groupId);
}

package com.example.satellite.repository;

import com.example.satellite.domain.NhqEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Description:
 * Created by Gaoxinwen on 2016/5/3.
 */
public interface NhqRepository extends JpaRepository<NhqEntity, Integer> {
    List<NhqEntity> findByStationIdAndGroupId(Integer stationId, Integer groupId);
}

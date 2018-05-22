package com.example.satellite.repository;

import com.example.satellite.domain.Poz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Description:
 * Created by Gaoxinwen on 2016/9/8.
 */
public interface PozRepository extends JpaRepository<Poz, Integer> {
    List<Poz> findByStationIdAndUnitId(Integer stationId, Integer unitId);
}

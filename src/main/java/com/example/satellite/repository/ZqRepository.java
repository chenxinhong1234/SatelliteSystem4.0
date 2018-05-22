package com.example.satellite.repository;

import com.example.satellite.domain.ZqEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Description:
 * Created by Gaoxinwen on 2016/5/3.
 */
public interface ZqRepository extends JpaRepository<ZqEntity, Integer>{
    List<ZqEntity> findByStationId(Integer stationId);
}

package com.example.satellite.repository;

import com.example.satellite.domain.Eotable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Description:
 * Created by Gaoxinwen on 2016/6/18.
 */
public interface EotableRepository extends JpaRepository<Eotable, Integer>{
    Eotable findByHeadAndSumN(Double head, Double sumN);
    List<Eotable> findByHead(Double head);
}

package com.example.satellite.repository;

import com.example.satellite.domain.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

/**
 * Description:
 * Created by Gaoxinwen on 2017/3/21.
 */
public interface ReportRepository extends JpaRepository<Report, Integer> {
    @Query("select b from Report b " +
            "where b.dispatchDate between ?1 and ?2")
    List<Report> findByDatesBetween(Date start, Date end);


    Report findByName(String name);
}

package com.example.satellite.service;

import com.example.satellite.domain.Report;
import com.example.satellite.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Description:
 * Created by Gaoxinwen on 2017/3/21.
 */
@Service
public class ReportService {
    @Autowired
    private ReportRepository reportRepository;

    public List<Report> getReportFromDate(Date start, Date end) {
        return reportRepository.findByDatesBetween(start, end);
    }

    public Report getReport(Integer id) {
        return reportRepository.findOne(id);
    }

    public List<Report> getAll() {
        return reportRepository.findAll();
    }

    public void deleteById(Integer id) {
        reportRepository.delete(id);
    }
}

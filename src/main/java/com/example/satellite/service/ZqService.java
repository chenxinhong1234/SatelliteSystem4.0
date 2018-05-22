package com.example.satellite.service;

import com.example.satellite.domain.ZqEntity;
import com.example.satellite.repository.ZqRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Parameters:
 * @Return:
 * @Description:
 * @Author: shushengmao
 * @Created: 2017-03-18-4:46 PM
 */
@Service
public class ZqService {
    @Autowired
    private ZqRepository zqRepository;

    public List<ZqEntity> findAll() {
        return zqRepository.findAll();
    }
}
package com.example.satellite.service;

import com.example.satellite.domain.HeoEntity;
import com.example.satellite.repository.HeoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Parameters:
 * @Return:
 * @Description:
 * @Author: shushengmao
 * @Created: 2017-03-20-11:01 AM
 */
@Service
public class HeoService {
    @Autowired
    private HeoRepository HeoRepository;

    public List<HeoEntity> findAll() {
        return HeoRepository.findAll();
    }
}

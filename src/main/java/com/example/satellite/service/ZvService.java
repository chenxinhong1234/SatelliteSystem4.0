package com.example.satellite.service;

import com.example.satellite.domain.ZvEntity;
import com.example.satellite.repository.ZvRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 在这个程序里，这个service去掉也可以，没什么用
 * Description:
 * Created by Gaoxinwen on 2016/12/26.
 */
@Service
public class ZvService {
    @Autowired
    private ZvRepository zvRepository;

    public List<ZvEntity> findAll() {
        return zvRepository.findAll();
    }
}

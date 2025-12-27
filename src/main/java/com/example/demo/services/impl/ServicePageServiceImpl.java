package com.example.demo.services.impl;

import com.example.demo.model.ServiceEntity;
import com.example.demo.repository.ServiceRepository;
import com.example.demo.services.ServicePageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServicePageServiceImpl implements ServicePageService {

    private final ServiceRepository serviceRepository;

    @Override
    public List<ServiceEntity> getActiveServices() {
        return serviceRepository.findAllByIsActiveTrueOrderByIdDesc();
    }
}

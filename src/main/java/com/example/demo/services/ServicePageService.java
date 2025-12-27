package com.example.demo.services;

import com.example.demo.model.ServiceEntity;

import java.util.List;

public interface ServicePageService {

    List<ServiceEntity> getActiveServices();
}

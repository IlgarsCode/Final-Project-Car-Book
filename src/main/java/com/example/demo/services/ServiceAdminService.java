package com.example.demo.services;

import com.example.demo.model.ServiceEntity;

import java.util.List;

public interface ServiceAdminService {
    List<ServiceEntity> getAll();
    ServiceEntity getById(Long id);
    void create(ServiceEntity service);
    void update(Long id, ServiceEntity service);
    void delete(Long id);
}

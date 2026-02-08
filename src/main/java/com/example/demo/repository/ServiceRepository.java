package com.example.demo.repository;

import com.example.demo.model.ServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceRepository extends JpaRepository<ServiceEntity, Long> {

    List<ServiceEntity> findAllByIsActiveTrueOrderByIdDesc();

    List<ServiceEntity> findAllByOrderByIdDesc();
}

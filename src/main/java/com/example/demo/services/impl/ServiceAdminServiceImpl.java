package com.example.demo.services.impl;

import com.example.demo.model.ServiceEntity;
import com.example.demo.repository.ServiceRepository;
import com.example.demo.services.ServiceAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceAdminServiceImpl implements ServiceAdminService {

    private final ServiceRepository repo;

    @Override
    public List<ServiceEntity> getAll() {
        return repo.findAllByOrderByIdDesc();
    }

    @Override
    public ServiceEntity getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Service tapılmadı: " + id));
    }

    @Override
    public void create(ServiceEntity service) {
        if (service.getIsActive() == null) service.setIsActive(true);
        repo.save(service);
    }

    @Override
    public void update(Long id, ServiceEntity service) {
        ServiceEntity db = getById(id);
        db.setTitle(service.getTitle());
        db.setDescription(service.getDescription());
        db.setIcon(service.getIcon());
        db.setIsActive(service.getIsActive() != null ? service.getIsActive() : db.getIsActive());
        repo.save(db);
    }

    @Override
    public void delete(Long id) {
        repo.deleteById(id);
    }
}

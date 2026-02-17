package com.example.demo.services.admin.impl;

import com.example.demo.dto.segment.CarSegmentCreateDto;
import com.example.demo.dto.segment.CarSegmentDashboardRowDto;
import com.example.demo.dto.segment.CarSegmentUpdateDto;
import com.example.demo.model.CarSegment;
import com.example.demo.repository.CarRepository;
import com.example.demo.repository.CarSegmentRepository;
import com.example.demo.services.admin.CarSegmentAdminService;
import com.example.demo.util.SlugUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CarSegmentAdminServiceImpl implements CarSegmentAdminService {

    private final CarSegmentRepository carSegmentRepository;
    private final CarRepository carRepository;

    @Override
    public List<CarSegmentDashboardRowDto> getAllRows() {
        return carSegmentRepository.findAllWithActiveCarCount()
                .stream()
                .map(v -> {
                    CarSegmentDashboardRowDto dto = new CarSegmentDashboardRowDto();
                    dto.setId(v.getId());
                    dto.setName(v.getName());
                    dto.setSlug(v.getSlug());
                    dto.setActiveCarCount(v.getActiveCarCount() == null ? 0L : v.getActiveCarCount());
                    return dto;
                })
                .sorted(Comparator.comparing(CarSegmentDashboardRowDto::getName, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    @Override
    public CarSegment getById(Long id) {
        return carSegmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Segment tapılmadı"));
    }

    @Override
    public void create(CarSegmentCreateDto dto) {
        String name = clean(dto.getName());

        if (carSegmentRepository.existsByNameIgnoreCase(name)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bu adla segment artıq var");
        }

        String slug = uniqueSlug(SlugUtil.slugify(name), null);

        CarSegment s = new CarSegment();
        s.setName(name);
        s.setSlug(slug);
        carSegmentRepository.save(s);
    }

    @Override
    public void update(Long id, CarSegmentUpdateDto dto) {
        CarSegment s = getById(id);
        String name = clean(dto.getName());

        if (!s.getName().equalsIgnoreCase(name) && carSegmentRepository.existsByNameIgnoreCase(name)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bu adla segment artıq var");
        }

        s.setName(name);

        String newBase = SlugUtil.slugify(name);
        String newSlug = uniqueSlug(newBase, s.getSlug());
        s.setSlug(newSlug);

        carSegmentRepository.save(s);
    }

    @Override
    public void delete(Long id) {
        CarSegment s = getById(id);

        long carsCount = carRepository.countBySegment_Id(s.getId());
        if (carsCount > 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Bu segment-də car var (" + carsCount + "). Əvvəl cars-ı başqa segment-ə keçir və ya sil."
            );
        }

        carSegmentRepository.delete(s);
    }

    private String uniqueSlug(String base, String currentSlug) {
        String slug = base;
        int i = 2;
        while (slug.isBlank() || (carSegmentRepository.existsBySlug(slug) && !slug.equals(currentSlug))) {
            slug = base + "-" + i++;
        }
        return slug;
    }

    private static String clean(String s) {
        if (s == null) return "";
        return s.trim().replaceAll("\\s+", " ");
    }
}

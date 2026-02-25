package com.test.baxolash.controller;

import com.test.baxolash.dto.DistrictDto;
import com.test.baxolash.dto.RegionDto;
import com.test.baxolash.service.DistrictService;
import com.test.baxolash.service.RegionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/regions")
@RequiredArgsConstructor
@Tag(name = "Регионы и районы Узбекистана", description = "Справочники для фильтров заявок")
public class RegionController {

    private final RegionService regionService;
    private final DistrictService districtService;

    @GetMapping
    @Operation(summary = "Список всех регионов")
    public ResponseEntity<List<RegionDto>> getAll() {
        return ResponseEntity.ok(regionService.findAll());
    }

    @GetMapping("/{regionId}/districts")
    @Operation(summary = "Районы и города по региону")
    public ResponseEntity<List<DistrictDto>> getDistricts(@PathVariable String regionId) {
        return ResponseEntity.ok(districtService.findByRegionId(regionId));
    }
}

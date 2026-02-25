package com.test.baxolash.service;

import com.test.baxolash.dto.DistrictDto;

import java.util.List;

public interface DistrictService {

    List<DistrictDto> findByRegionId(String regionId);
}

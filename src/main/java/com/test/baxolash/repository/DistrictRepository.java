package com.test.baxolash.repository;

import com.test.baxolash.entity.District;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DistrictRepository extends JpaRepository<District, String> {

    List<District> findByRegion_IdAndDeletedAtIsNullOrderBySortOrderAscNameUzAsc(String regionId);
}

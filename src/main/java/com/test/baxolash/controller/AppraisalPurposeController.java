package com.test.baxolash.controller;

import com.test.baxolash.dto.AppraisalPurposeDto;
import com.test.baxolash.service.AppraisalPurposeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/appraisal-purposes")
@RequiredArgsConstructor
@Tag(name = "Цели оценки", description = "Справочник целей оценки (Баҳолаш мақсади) на 3 языках")
public class AppraisalPurposeController {

    private final AppraisalPurposeService appraisalPurposeService;

    @GetMapping
    @Operation(summary = "Список целей оценки (nameUz, nameRu, nameEn)")
    public ResponseEntity<List<AppraisalPurposeDto>> getAll() {
        return ResponseEntity.ok(appraisalPurposeService.findAll());
    }
}

package com.test.baxolash.service;

import com.test.baxolash.dto.EvaluationRequestCreateDto;
import com.test.baxolash.dto.EvaluationRequestDto;
import com.test.baxolash.dto.EvaluationRequestUpdateDto;
import com.test.baxolash.dto.FileDownloadResult;
import com.test.baxolash.entity.enums.EvaluationRequestStatus;
import com.test.baxolash.entity.EvaluationRequestType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Заявки на оценку.
 * Права: клиент (CLIENT_EMPLOYEE) — только свои заявки, создание, загрузка документов, просмотр, скачивание отчёта.
 * Сотрудник компании (COMPANY_EMPLOYEE) — все заявки, фильтр, редактирование, статус, отчёт, стоимость, подтверждение.
 */
public interface EvaluationRequestService {

    EvaluationRequestDto create(EvaluationRequestCreateDto dto);

    /** Создание заявки с документом: REAL_ESTATE — кадастр, VEHICLE — техпаспорт, FIXED_ASSETS — документы объекта. */
    EvaluationRequestDto createWithDocument(EvaluationRequestCreateDto dto, MultipartFile cadastralDocument,
                                           MultipartFile techPassportDocument, MultipartFile fixedAssetsDocument);

    Page<EvaluationRequestDto> getMy(Pageable pageable);

    Page<EvaluationRequestDto> getMy(Pageable pageable, EvaluationRequestType requestType, EvaluationRequestStatus status,
                                     String regionId, String districtId, String search, java.time.Instant dateFrom, java.time.Instant dateTo);

    Page<EvaluationRequestDto> getAll(Pageable pageable, EvaluationRequestStatus statusFilter);

    EvaluationRequestDto getById(String id);

    EvaluationRequestDto update(String id, EvaluationRequestUpdateDto dto);

    void uploadDocument(String requestId, MultipartFile file);

    void uploadReport(String requestId, MultipartFile file);

    void confirmCompletion(String requestId);

    FileDownloadResult downloadReport(String requestId);

    void deleteReport(String requestId);

    FileDownloadResult downloadDocument(String documentId);

    List<EvaluationRequestStatus> getStatusesForFilter();
}

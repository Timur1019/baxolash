package com.test.baxolash.controller;

import com.test.baxolash.dto.ContactRequestCreateDto;
import com.test.baxolash.dto.ContactRequestDto;
import com.test.baxolash.service.ContactRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Обратная связь", description = "Заявки с формы контактов")
public class ContactRequestController {

    private final ContactRequestService contactRequestService;

    @PostMapping(value = "/contact", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Отправить заявку с сайта (публичный)")
    public ResponseEntity<ContactRequestDto> submit(@Valid @RequestBody ContactRequestCreateDto dto) {
        ContactRequestDto created = contactRequestService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/admin/contact-requests")
    @Operation(summary = "Список заявок (только для админа)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ContactRequestDto>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(contactRequestService.getAll(pageable));
    }

    @DeleteMapping("/admin/contact-requests/{id}")
    @Operation(summary = "Удалить одну заявку (только для админа)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteOne(@PathVariable String id) {
        contactRequestService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/admin/contact-requests")
    @Operation(summary = "Удалить все заявки (только для админа)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAll() {
        contactRequestService.deleteAll();
        return ResponseEntity.noContent().build();
    }
}

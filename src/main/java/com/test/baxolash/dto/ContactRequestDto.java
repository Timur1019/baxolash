package com.test.baxolash.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactRequestDto {

    private String id;
    private String name;
    private String email;
    private String phone;
    private String subject;
    private String message;
    private Instant createdAt;
}

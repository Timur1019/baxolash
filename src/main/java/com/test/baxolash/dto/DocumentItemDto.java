package com.test.baxolash.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentItemDto {

    private String id;
    private String fileName;
    /** Публичный URL файла в R2 (для скачивания/просмотра) */
    private String fileUrl;
    private String uploadedByFullName;
    private String createdAt;
    /** true — загружено клиентом при создании заявки; false — сотрудником компании */
    private Boolean fromClient;
}

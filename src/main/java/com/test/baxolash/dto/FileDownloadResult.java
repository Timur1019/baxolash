package com.test.baxolash.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.InputStream;

@Getter
@AllArgsConstructor
public class FileDownloadResult {

    private final InputStream inputStream;
    private final String fileName;
}

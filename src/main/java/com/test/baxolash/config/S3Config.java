package com.test.baxolash.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;

@Configuration
@ConditionalOnProperty(name = "S3_ENDPOINT")
public class S3Config {

    @Value("${S3_ACCESS_KEY:}")
    private String accessKey;

    @Value("${S3_SECRET_KEY:}")
    private String secretKey;

    @Value("${S3_REGION:auto}")
    private String region;

    @Value("${S3_ENDPOINT:}")
    private String endpoint;

    @Bean
    public S3Client s3Client() {
        if (endpoint == null || endpoint.isBlank()) {
            throw new IllegalStateException("S3_ENDPOINT is required when S3 config is active");
        }
        AwsBasicCredentials credentials = AwsBasicCredentials.create(
                accessKey != null ? accessKey : "",
                secretKey != null ? secretKey : ""
        );
        S3Configuration serviceConfig = S3Configuration.builder()
                .pathStyleAccessEnabled(true)
                .chunkedEncodingEnabled(false)
                .build();

        return S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .region(Region.of(region != null && !region.isBlank() ? region : "auto"))
                .endpointOverride(URI.create(endpoint))
                .serviceConfiguration(serviceConfig)
                .build();
    }
}
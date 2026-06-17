package com.example.onlineedu.config;

import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class AzureStorageConfig {

    @Value("${azure.storage.connection-string:}")
    private String connectionString;

    @Value("${azure.storage.container-name:videos}")
    private String containerName;

    @Bean
    public BlobServiceClient blobServiceClient() {
        if (connectionString == null || connectionString.isBlank()) {
            log.warn("Azure Storage connection string not configured, Azure Blob Storage will be disabled");
            return null;
        }
        return new BlobServiceClientBuilder()
                .connectionString(connectionString)
                .buildClient();
    }

    public String getContainerName() {
        return containerName;
    }
}

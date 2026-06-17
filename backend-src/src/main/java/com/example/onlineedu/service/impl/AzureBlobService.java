package com.example.onlineedu.service.impl;

import com.azure.storage.blob.*;
import com.azure.storage.blob.models.*;
import com.azure.storage.sas.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;

@Slf4j
@Service
public class AzureBlobService {

    @Autowired(required = false)
    private BlobServiceClient blobServiceClient;

    @Autowired
    private com.example.onlineedu.config.AzureStorageConfig azureStorageConfig;

    /**
     * 上传视频到 Azure Blob Storage
     * @return 带 SAS token的视频访问 URL
     */
    public String uploadVideo(MultipartFile file) {
        if (blobServiceClient == null) {
            throw new RuntimeException("Azure Blob Storage 未配置，请在 application.yml 中设置 azure.storage.connection-string");
        }

        String containerName = azureStorageConfig.getContainerName();
        String originalFilename = file.getOriginalFilename();
        String blobName = "videos/" + System.currentTimeMillis() + "_" + originalFilename;

        try {
            BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
            // 自动创建容器（如果不存在）
            if (!containerClient.exists()) {
                containerClient.create();
                log.info("已创建 Azure Blob 容器: {}", containerName);
            }

            BlockBlobClient blobClient = containerClient.getBlobClient(blobName).getBlockBlobClient();

            try (InputStream inputStream = file.getInputStream()) {
                blobClient.upload(inputStream, file.getSize());
            }

            // 设置 Content-Type
            blobClient.setHttpHeaders(new BlobHttpHeaders().setContentType(file.getContentType()));

            log.info("视频上传到 Azure Blob 成功: {}", blobName);

            // 返回带 SAS 令牌的访问 URL（有效期 24 小时）
            return generateSasUrl(containerName, blobName, 24 * 60 * 60);

        } catch (Exception e) {
            log.error("上传视频到 Azure Blob 失败: {}", originalFilename, e);
            throw new RuntimeException("上传视频失败: " + e.getMessage());
        }
    }

    /**
     * 生成 SAS 访问 URL（免公网带宽，直接 CDN 加速）
     */
    public String generateSasUrl(String containerName, String blobName, int validSeconds) {
        if (blobServiceClient == null) {
            return null;
        }

        try {
            BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
            BlockBlobClient blobClient = containerClient.getBlobClient(blobName).getBlockBlobClient();

            OffsetDateTime expiryTime = OffsetDateTime.now().plusSeconds(validSeconds);

            String sasToken = blobClient.generateSas(
                    new BlobServiceSasSignatureValues()
                            .setPermissions(new BlobContainerSasPermission().setReadPermission(true))
                            .setExpiryTime(expiryTime)
            );

            return blobClient.getBlobUrl() + "?" + sasToken;

        } catch (Exception e) {
            log.error("生成 SAS URL 失败: {}", blobName, e);
            return null;
        }
    }

    /**
     * 删除 Azure Blob 中的视频
     */
    public boolean deleteVideo(String blobUrl) {
        if (blobServiceClient == null || blobUrl == null) {
            return false;
        }

        try {
            String containerName = azureStorageConfig.getContainerName();
            // 从 URL 中解析 blobName
            String prefix = containerName + "/";
            int idx = blobUrl.indexOf(prefix);
            if (idx < 0) return false;
            String blobName = blobUrl.substring(idx + prefix.length()).split("\?")[0];

            BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
            BlockBlobClient blobClient = containerClient.getBlobClient(blobName).getBlockBlobClient();
            blobClient.deleteIfExists();
            log.info("已从 Azure Blob 删除视频: {}", blobName);
            return true;
        } catch (Exception e) {
            log.error("删除 Azure Blob 视频失败: {}", blobUrl, e);
            return false;
        }
    }
}

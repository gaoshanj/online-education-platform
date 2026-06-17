package com.example.onlineedu.utils;

import com.example.onlineedu.common.ErrorCode;
import com.example.onlineedu.config.FileUploadConfig;
import com.example.onlineedu.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.UUID;

/**
 * 文件上传工具类
 * 处理文件上传逻辑
 *
 * @author feng
 */
@Slf4j
@Component
public class FileUtil {

    @Autowired
    private FileUploadConfig fileUploadConfig;

    /**
     * 上传文件
     *
     * @param file 文件
     * @param type 文件类型（image/video）
     * @return 文件访问URL
     */
    public String uploadFile(MultipartFile file, String type) {
        // 1. 校验文件是否为空
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.FILE_UPLOAD_ERROR, "文件不能为空");
        }

        // 2. 校验文件大小
        long fileSize = file.getSize();
        long maxSize = fileUploadConfig.getMaxSize() * 1024 * 1024; // 转换为字节
        if (fileSize > maxSize) {
            throw new BusinessException(ErrorCode.FILE_SIZE_ERROR,
                    "文件大小超出限制，最大允许" + fileUploadConfig.getMaxSize() + "MB");
        }

        // 3. 校验文件类型
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new BusinessException(ErrorCode.FILE_UPLOAD_ERROR, "文件名不能为空");
        }

        String extension = getFileExtension(originalFilename);
        if (!isAllowedFileType(extension, type)) {
            throw new BusinessException(ErrorCode.FILE_TYPE_ERROR, "不支持的文件类型：" + extension);
        }

        // 4. 生成文件存储路径（按日期分目录）
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String uploadPath = fileUploadConfig.getPath() + File.separator + type + File.separator + datePath;

        // 5. 创建目录
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // 6. 生成唯一文件名
        String fileName = generateFileName(extension);
        String filePath = uploadPath + File.separator + fileName;

        // 7. 保存文件
        try {
            file.transferTo(new File(filePath));
            log.info("文件上传成功：{}", filePath);
        } catch (IOException e) {
            log.error("文件上传失败：{}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.FILE_UPLOAD_ERROR, "文件上传失败：" + e.getMessage());
        }

        // 8. 返回文件访问URL
        return fileUploadConfig.getUrlPrefix() + "/" + type + "/" + datePath + "/" + fileName;
    }

    /**
     * 获取文件扩展名
     *
     * @param filename 文件名
     * @return 扩展名（小写，不含点）
     */
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1).toLowerCase();
    }

    /**
     * 校验文件类型是否允许
     *
     * @param extension 文件扩展名
     * @param type      文件类型（image/video）
     * @return true 允许，false 不允许
     */
    private boolean isAllowedFileType(String extension, String type) {
        if ("image".equals(type)) {
            return Arrays.asList(fileUploadConfig.getImageTypes()).contains(extension);
        } else if ("video".equals(type)) {
            return Arrays.asList(fileUploadConfig.getVideoTypes()).contains(extension);
        }
        return false;
    }

    /**
     * 生成唯一文件名
     *
     * @param extension 文件扩展名
     * @return 文件名
     */
    private String generateFileName(String extension) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String timestamp = String.valueOf(System.currentTimeMillis());
        return uuid + "_" + timestamp + "." + extension;
    }

    /**
     * 删除文件
     *
     * @param fileUrl 文件URL
     * @return true 成功，false 失败
     */
    public boolean deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return false;
        }

        try {
            // 从URL中提取文件路径
            String filePath = fileUrl.replace(fileUploadConfig.getUrlPrefix(), fileUploadConfig.getPath());
            File file = new File(filePath);

            if (file.exists() && file.isFile()) {
                boolean deleted = file.delete();
                if (deleted) {
                    log.info("文件删除成功：{}", filePath);
                } else {
                    log.warn("文件删除失败：{}", filePath);
                }
                return deleted;
            } else {
                log.warn("文件不存在：{}", filePath);
                return false;
            }
        } catch (Exception e) {
            log.error("文件删除失败：{}", e.getMessage(), e);
            return false;
        }
    }
}

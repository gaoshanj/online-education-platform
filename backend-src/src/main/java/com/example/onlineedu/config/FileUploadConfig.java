package com.example.onlineedu.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

/**
 * 文件上传配置类
 * 从配置文件中读取文件上传相关配置
 *
 * @author feng
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "file.upload")
public class FileUploadConfig {

    /**
     * 文件上传路径
     */
    private String path;

    /**
     * 文件访问URL前缀
     */
    private String urlPrefix;

    /**
     * 最大文件大小（MB）
     */
    private Integer maxSize;

    /**
     * 允许的图片类型
     */
    private String[] imageTypes;

    /**
     * 允许的视频类型
     */
    private String[] videoTypes;
}

package com.example.onlineedu.controller.common;

import com.example.onlineedu.common.Result;
import com.example.onlineedu.service.impl.AzureBlobService;
import com.example.onlineedu.utils.FileUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传控制器
 * 提供文件上传接口
 *
 * @author feng
 */
@Slf4j
@Api(tags = "文件上传接口")
@RestController
@RequestMapping("/api/common/file")
public class FileController {

    @Autowired
    private FileUtil fileUtil;

    @Autowired(required = false)
    private AzureBlobService azureBlobService;

    /**
     * 上传图片（仍使用本地存储）
     */
    @ApiOperation("上传图片")
    @PostMapping("/upload/image")
    public Result<String> uploadImage(
            @ApiParam("图片文件") @RequestParam("file") MultipartFile file) {
        log.info("开始上传图片，文件名：{}", file.getOriginalFilename());
        String fileUrl = fileUtil.uploadFile(file, "image");
        return Result.success("图片上传成功", fileUrl);
    }

    /**
     * 上传视频（使用 Azure Blob Storage，支持视频流）
     */
    @ApiOperation("上传视频（Azure Blob Storage）")
    @PostMapping("/upload/video")
    public Result<String> uploadVideo(
            @ApiParam("视频文件") @RequestParam("file") MultipartFile file) {
        log.info("开始上传视频到 Azure Blob，文件名：{}", file.getOriginalFilename());

        if (azureBlobService == null) {
            // 降级：Azure 未配置时仍使用本地存储
            log.warn("Azure Blob 未配置，降级使用本地存储");
            String fileUrl = fileUtil.uploadFile(file, "video");
            return Result.success("视频上传成功（本地存储）", fileUrl);
        }

        try {
            String azureUrl = azureBlobService.uploadVideo(file);
            return Result.success("视频上传成功", azureUrl);
        } catch (Exception e) {
            log.error("Azure Blob 上传失败，降级使用本地存储", e);
            String fileUrl = fileUtil.uploadFile(file, "video");
            return Result.success("视频上传成功（降级至本地存储）", fileUrl);
        }
    }

    /**
     * 删除文件（同时支持本地和 Azure Blob）
     */
    @ApiOperation("删除文件")
    @DeleteMapping("/delete")
    public Result<Void> deleteFile(@ApiParam("文件URL") @RequestParam("fileUrl") String fileUrl) {
        log.info("开始删除文件：{}", fileUrl);

        // 判断是否是 Azure Blob URL
        if (fileUrl != null && fileUrl.contains("blob.core.windows.net")) {
            if (azureBlobService != null) {
                boolean success = azureBlobService.deleteVideo(fileUrl);
                if (success) {
                    return Result.success("文件删除成功", null);
                }
            }
        }

        // 本地文件删除（降级）
        boolean success = fileUtil.deleteFile(fileUrl);
        if (success) {
            return Result.success("文件删除成功", null);
        } else {
            return Result.error("文件删除失败");
        }
    }
}

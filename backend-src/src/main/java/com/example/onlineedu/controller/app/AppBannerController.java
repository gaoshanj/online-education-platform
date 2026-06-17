package com.example.onlineedu.controller.app;

import com.example.onlineedu.common.Result;
import com.example.onlineedu.domain.vo.BannerVO;
import com.example.onlineedu.service.BannerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * App端 - 首页轮播图控制器
 *
 * @author feng
 */
@Slf4j
@Api(tags = "用户端-首页轮播图")
@RestController
@RequestMapping("/api/app/banner")
public class AppBannerController {

    @Autowired
    private BannerService bannerService;

    @ApiOperation("获取首页轮播图列表（仅启用状态，按sort排序）")
    @GetMapping("/list")
    public Result<List<BannerVO>> listBanners() {
        return Result.success(bannerService.listEnabledBanners());
    }
}

package com.example.onlineedu.controller.app;

import com.example.onlineedu.common.Result;
import com.example.onlineedu.domain.model.LoginUser;
import com.example.onlineedu.domain.vo.CourseVO;
import com.example.onlineedu.service.RecommendService;
import com.example.onlineedu.utils.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 首页课程推荐控制器
 * 提供四个独立区块接口：热门课程 / 最新上线 / 精品课程 / 为你推荐
 *
 * @author feng
 */
@Slf4j
@Api(tags = "用户端-首页推荐")
@RestController
@RequestMapping("/api/app/recommend")
public class AppRecommendController {

    @Autowired
    private RecommendService recommendService;

    /**
     * 热门课程
     * 未登录/已登录均可访问，按综合热度得分降序
     */
    @ApiOperation("热门课程推荐")
    @GetMapping("/hot")
    public Result<List<CourseVO>> hot(
            @ApiParam("返回数量") @RequestParam(defaultValue = "10") int limit) {
        return Result.success(recommendService.getHotRecommend(limit));
    }

    /**
     * 最新上线课程
     * 未登录/已登录均可访问，按发布时间降序
     */
    @ApiOperation("最新上线课程")
    @GetMapping("/latest")
    public Result<List<CourseVO>> latest(
            @ApiParam("返回数量") @RequestParam(defaultValue = "10") int limit) {
        return Result.success(recommendService.getLatestRecommend(limit));
    }

    /**
     * 精品课程
     * 未登录展示（后台设置：is_recommend=1）
     */
    @ApiOperation("精品课程推荐")
    @GetMapping("/featured")
    public Result<List<CourseVO>> featured(
            @ApiParam("返回数量") @RequestParam(defaultValue = "10") int limit) {
        return Result.success(recommendService.getFeaturedRecommend(limit));
    }

    /**
     * 个性化推荐（为你推荐）
     * - 未登录不展示
     * - 已登录且行为数 > 1：ItemCF
     * - 冷启动（行为数 < 1）：降级返回综合排序课程
     */
    @ApiOperation("个性化课程推荐（为你推荐）")
    @GetMapping("/personal")
    public Result<List<CourseVO>> personal(
            @ApiParam("返回数量") @RequestParam(defaultValue = "10") int limit) {
        LoginUser currentUser = UserContext.getUser();
        Long userId = currentUser != null ? currentUser.getUserId() : null;
        return Result.success(recommendService.getPersonalRecommend(userId, limit));
    }
}

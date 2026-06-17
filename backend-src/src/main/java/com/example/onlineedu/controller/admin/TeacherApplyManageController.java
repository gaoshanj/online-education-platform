package com.example.onlineedu.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.onlineedu.annotation.RequireRole;
import com.example.onlineedu.common.Result;
import com.example.onlineedu.domain.enums.UserRole;
import com.example.onlineedu.domain.vo.TeacherApplicationVO;
import com.example.onlineedu.service.TeacherApplicationService;
import com.example.onlineedu.utils.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理端讲师申请审核控制器
 *
 * @author feng
 */
@Slf4j
@Api(tags = "管理端-讲师入驻审核")
@RestController
@RequestMapping("/api/admin/teacher-apply")
public class TeacherApplyManageController {

    @Autowired
    private TeacherApplicationService applicationService;

    @ApiOperation("分页查询申请列表")
    @GetMapping("/page")
    @RequireRole({ UserRole.ADMIN })
    public Result<Page<TeacherApplicationVO>> page(
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam("状态") @RequestParam(required = false) Integer status) {
        Page<TeacherApplicationVO> result = applicationService.pageApplications(page, size, status);
        return Result.success(result);
    }

    @ApiOperation("查看申请详情")
    @GetMapping("/detail/{id}")
    @RequireRole({ UserRole.ADMIN })
    public Result<TeacherApplicationVO> getDetail(@PathVariable Long id) {
        TeacherApplicationVO vo = applicationService.getDetail(id);
        return Result.success(vo);
    }

    @ApiOperation("审核通过")
    @PutMapping("/approve/{id}")
    @RequireRole({ UserRole.ADMIN })
    public Result<Void> approve(@PathVariable Long id) {
        Long auditorId = UserContext.getUserId();
        applicationService.approve(id, auditorId);
        return Result.success();
    }

    @ApiOperation("审核拒绝")
    @PutMapping("/reject/{id}")
    @RequireRole({ UserRole.ADMIN })
    public Result<Void> reject(
            @PathVariable Long id,
            @ApiParam("拒绝原因") @RequestParam String rejectReason) {
        Long auditorId = UserContext.getUserId();
        applicationService.reject(id, auditorId, rejectReason);
        return Result.success();
    }
}

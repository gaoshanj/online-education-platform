package com.example.onlineedu.controller.app;

import com.example.onlineedu.annotation.RequireRole;
import com.example.onlineedu.common.Result;
import com.example.onlineedu.domain.dto.TeacherApplicationDTO;
import com.example.onlineedu.domain.enums.UserRole;
import com.example.onlineedu.domain.vo.TeacherApplicationVO;
import com.example.onlineedu.service.TeacherApplicationService;
import com.example.onlineedu.utils.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 用户端讲师申请控制器
 *
 * @author feng
 */
@Slf4j
@Api(tags = "用户端-讲师申请")
@RestController
@RequestMapping("/api/app/teacher-apply")
public class AppTeacherApplyController {

    @Autowired
    private TeacherApplicationService applicationService;

    @ApiOperation("首次提交讲师申请")
    @PostMapping("/submit")
    @RequireRole({ UserRole.STUDENT })
    public Result<Void> submit(@RequestBody @Valid TeacherApplicationDTO dto) {
        Long userId = UserContext.getUserId();
        applicationService.submitApplication(dto, userId);
        return Result.success();
    }

    @ApiOperation("修改申请信息并重新提交（已拒绝/已取消资格状态可用）")
    @PutMapping("/update")
    @RequireRole({ UserRole.STUDENT })
    public Result<Void> update(@RequestBody @Valid TeacherApplicationDTO dto) {
        Long userId = UserContext.getUserId();
        applicationService.updateApplication(dto, userId);
        return Result.success();
    }

    @ApiOperation("查看我的申请状态")
    @GetMapping("/my-status")
    @RequireRole({ UserRole.STUDENT })
    public Result<TeacherApplicationVO> getMyStatus() {
        Long userId = UserContext.getUserId();
        TeacherApplicationVO vo = applicationService.getMyApplication(userId);
        return Result.success(vo);
    }
}

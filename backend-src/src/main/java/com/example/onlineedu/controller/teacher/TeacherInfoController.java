package com.example.onlineedu.controller.teacher;

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
 * 讲师端 - 认证信息管理控制器
 * 讲师可查看/修改自己当时申请时填写的认证信息，修改后需重新审核（状态变为3-修改待审核）
 *
 * @author feng
 */
@Slf4j
@Api(tags = "讲师端-认证信息管理")
@RestController
@RequestMapping("/api/teacher/info")
public class TeacherInfoController {

    @Autowired
    private TeacherApplicationService applicationService;

    @ApiOperation("查看自己的认证信息（申请记录详情）")
    @GetMapping("/certification")
    @RequireRole({ UserRole.TEACHER })
    public Result<TeacherApplicationVO> getCertification() {
        Long userId = UserContext.getUserId();
        TeacherApplicationVO vo = applicationService.getMyApplication(userId);
        return Result.success(vo);
    }

    @ApiOperation("修改认证信息并重新提交审核（审核通过后生效，状态变为3-修改待审核）")
    @PutMapping("/certification")
    @RequireRole({ UserRole.TEACHER })
    public Result<Void> updateCertification(@RequestBody @Valid TeacherApplicationDTO dto) {
        Long userId = UserContext.getUserId();
        applicationService.updateByTeacher(dto, userId);
        return Result.success();
    }
}

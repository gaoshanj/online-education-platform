package com.example.onlineedu.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.onlineedu.annotation.RequireRole;
import com.example.onlineedu.common.Result;
import com.example.onlineedu.domain.dto.AdminCreateUserDTO;
import com.example.onlineedu.domain.dto.UpdateUserRolesDTO;
import com.example.onlineedu.domain.enums.UserRole;
import com.example.onlineedu.domain.vo.UserDetailVO;
import com.example.onlineedu.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理控制器
 *
 * @author feng
 */
@Slf4j
@Api(tags = "管理端-用户管理")
@RestController
@RequestMapping("/api/admin/user")
public class UserManageController {

    @Autowired
    private UserService userService;

    @ApiOperation("分页查询用户")
    @GetMapping("/page")
    @RequireRole({ UserRole.ADMIN })
    public Result<Page<UserDetailVO>> page(
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam("用户名") @RequestParam(required = false) String username,
            @ApiParam("状态") @RequestParam(required = false) Integer status) {
        Page<UserDetailVO> result = userService.pageUsers(page, size, username, status);
        return Result.success(result);
    }

    @ApiOperation("查询用户详情")
    @GetMapping("/detail/{id}")
    @RequireRole({ UserRole.ADMIN })
    public Result<UserDetailVO> getDetail(@PathVariable Long id) {
        UserDetailVO vo = userService.getUserDetail(id);
        return Result.success(vo);
    }

    @ApiOperation("禁用用户")
    @PutMapping("/disable/{id}")
    @RequireRole({ UserRole.ADMIN })
    public Result<Void> disable(@PathVariable Long id) {
        userService.disableUser(id);
        return Result.success();
    }

    @ApiOperation("启用用户")
    @PutMapping("/enable/{id}")
    @RequireRole({ UserRole.ADMIN })
    public Result<Void> enable(@PathVariable Long id) {
        userService.enableUser(id);
        return Result.success();
    }

    @ApiOperation("删除用户")
    @DeleteMapping("/delete/{id}")
    @RequireRole({ UserRole.ADMIN })
    public Result<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success();
    }

    @ApiOperation("重置用户密码")
    @PutMapping("/reset-password/{id}")
    @RequireRole({ UserRole.ADMIN })
    public Result<Void> resetPassword(@PathVariable Long id) {
        userService.resetPassword(id);
        return Result.success();
    }

    @ApiOperation("创建用户（管理员）")
    @PostMapping("/create")
    @RequireRole({ UserRole.ADMIN })
    public Result<Void> createUser(@Validated @RequestBody AdminCreateUserDTO dto) {
        userService.createUser(dto);
        return Result.success();
    }

    @ApiOperation("修改用户角色集合（管理员）")
    @PutMapping("/{id}/roles")
    @RequireRole({ UserRole.ADMIN })
    public Result<Void> updateUserRoles(
            @PathVariable Long id,
            @Validated @RequestBody UpdateUserRolesDTO dto) {
        userService.updateUserRoles(id, dto);
        return Result.success();
    }
}

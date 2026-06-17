package com.example.onlineedu.controller.common;

import com.example.onlineedu.common.Result;
import com.example.onlineedu.domain.dto.LoginDTO;
import com.example.onlineedu.domain.dto.RegisterDTO;
import com.example.onlineedu.domain.vo.LoginVO;
import com.example.onlineedu.domain.vo.UserInfoVO;
import com.example.onlineedu.service.UserService;
import com.example.onlineedu.utils.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * 认证控制器
 * 提供注册、登录、注销接口
 *
 * @author feng
 */
@Slf4j
@Api(tags = "认证相关接口")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @ApiOperation("发送邮箱验证码（scene: forgot=忘记密码）")
    @PostMapping("/send-email-code")
    public Result<Void> sendEmailCode(@RequestParam String email,
                                       @RequestParam String scene) {
        if ("register".equals(scene)) {
            return Result.error("注册功能已关闭，请联系管理员添加账号");
        }
        userService.sendEmailCode(email, scene);
        return Result.success();
    }

    @ApiOperation("用户注册（已关闭，仅管理员可添加账号）")
    @PostMapping("/register")
    public Result<String> register(@RequestBody @Valid RegisterDTO registerDTO) {
        return Result.error("注册功能已关闭，请联系管理员添加账号");
    }

    @ApiOperation("用户登录")
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody @Valid LoginDTO loginDTO) {
        LoginVO loginVO = userService.login(loginDTO);
        return Result.success("登录成功", loginVO);
    }

    @ApiOperation("用户注销")
    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        userService.logout(token);
        return Result.success("注销成功");
    }

    @ApiOperation("获取当前用户信息")
    @GetMapping("/info")
    public Result<UserInfoVO> getUserInfo() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.error("用户未登录");
        }
        UserInfoVO userInfo = userService.getUserInfo(userId);
        return Result.success(userInfo);
    }
}

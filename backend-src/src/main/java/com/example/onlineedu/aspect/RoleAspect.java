package com.example.onlineedu.aspect;

import com.example.onlineedu.annotation.RequireRole;
import com.example.onlineedu.common.ErrorCode;
import com.example.onlineedu.domain.enums.UserRole;
import com.example.onlineedu.domain.model.LoginUser;
import com.example.onlineedu.exception.BusinessException;
import com.example.onlineedu.utils.UserContext;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 角色权限切面
 *
 * @author feng
 */
@Aspect
@Component
public class RoleAspect {

    @Before("@annotation(requireRole)")
    public void checkRole(RequireRole requireRole) {

        LoginUser user = UserContext.getUser();

        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_LOGIN);
        }

        List<String> userRoles = user.getRoles();
        if (userRoles == null || userRoles.isEmpty()) {
            throw new BusinessException(ErrorCode.PERMISSION_DENIED);
        }

        // 检查是否有任一指定角色
        for (UserRole requiredRole : requireRole.value()) {
            if (userRoles.contains(requiredRole.name())) {
                return;
            }
        }

        throw new BusinessException(ErrorCode.PERMISSION_DENIED);
    }
}

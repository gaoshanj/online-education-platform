package com.example.onlineedu.interceptor;

import com.example.onlineedu.utils.UserContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

/**
 * 权限认证拦截器
 * 依赖 TokenParseInterceptor 的解析结果，只针对未在白名单的 API 进行组断鉴权
 *
 * @author feng
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {

        // 直接放行 OPTIONS 预检请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 由于 TokenParseInterceptor 已经全量执行过并尝试解析 Token 放入 Context
        // 这里只需要判断上下文中有没有解析好的用户信息即可
        if (UserContext.getUser() == null) {
            sendUnauthorized(response, "未登录或 token 失效");
            return false;
        }

        return true;
    }

    private void sendUnauthorized(HttpServletResponse response, String msg) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(401);
        response.getWriter().write("{\"code\":401,\"msg\":\"" + msg + "\"}");
    }
}

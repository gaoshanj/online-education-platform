package com.example.onlineedu.interceptor;

import com.example.onlineedu.domain.model.LoginUser;
import com.example.onlineedu.utils.JwtUtil;
import com.example.onlineedu.utils.RedisUtil;
import com.example.onlineedu.utils.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 全量 Token 解析拦截器（解决单个拦截器用户已登录时访问不需要登录的接口没法获取用户上下文的问题）
 * 拦截所有 /api/** 请求，仅负责解析 Token 并放入上下文，不阻断请求
 * @author feng
 */
@Component
public class TokenParseInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(TokenParseInterceptor.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisUtil redisUtil;

    private static final String REDIS_TOKEN_BLACKLIST_PREFIX = "token:blacklist:";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = request.getHeader("Authorization");

        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            token = token.substring(7);

            // 1. 检查黑名单
            if (redisUtil.hasKey(REDIS_TOKEN_BLACKLIST_PREFIX + token)) {
                return true; // 即使 token 在黑名单，也直接放行，由后续的权限拦截器去阻截
            }

            try {
                // 2. 验证并解析 Token
                if (jwtUtil.validateToken(token)) {
                    // 3. 构建 LoginUser
                    LoginUser user = new LoginUser();
                    user.setUserId(jwtUtil.getUserIdFromToken(token));
                    user.setUsername(jwtUtil.getUsernameFromToken(token));
                    user.setRoles(jwtUtil.getRolesFromToken(token));

                    // 4. 设置上下文
                    UserContext.setUser(user);
                }
            } catch (Exception e) {
                // Token 无效或过期，直接放行，留给后续拦截器处理
                log.debug("Token解析失败: {}", e.getMessage());
            }
        }

        // 总是放行
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 清理线程变量，防止内存泄漏
        UserContext.clear();
    }
}

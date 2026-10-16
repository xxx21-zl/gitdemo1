package com.example.demo1.config;

import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.example.demo1.annotation.RequireRole;
import com.example.demo1.common.BusinessException;
import com.example.demo1.enums.UserRole;
import com.example.demo1.util.JwtUtil;
import com.example.demo1.util.UserContext;
import io.jsonwebtoken.Claims;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 认证鉴权拦截器：解析 Authorization: Bearer {token}，写入用户上下文并做角色校验
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtUtil jwtUtil;

    public AuthInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean preHandle(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response,
                             @Nonnull Object handler) {
        // 非 Controller 方法（静态资源等）直接放行
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        // 清理残留上下文，避免线程复用导致的串号
        UserContext.clear();

        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization == null || !authorization.startsWith(BEARER_PREFIX)) {
            throw BusinessException.unauthorized("未登录，请先获取 token");
        }
        String token = authorization.substring(BEARER_PREFIX.length());

        Claims claims;
        try {
            claims = jwtUtil.parseToken(token);
        } catch (JwtUtil.BusinessJwtException e) {
            throw BusinessException.unauthorized(e.getMessage());
        }

        UserRole role = UserRole.valueOf(String.valueOf(claims.get("role")));
        UserContext.LoginUser loginUser =
                new UserContext.LoginUser(Long.parseLong(claims.getSubject()), claims.get("username", String.class), role);
        UserContext.set(loginUser);

        RequireRole requireRole = handlerMethod.getMethodAnnotation(RequireRole.class);
        if (requireRole == null) {
            requireRole = handlerMethod.getBeanType().getAnnotation(RequireRole.class);
        }
        if (requireRole != null && !matchRole(requireRole.value(), role)) {
            throw BusinessException.forbidden("无访问权限，需要角色：" + String.join(",", roleCodes(requireRole.value())));
        }
        return true;
    }

    @Override
    public void afterCompletion(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response,
                                @Nonnull Object handler, Exception ex) {
        UserContext.clear();
    }

    private boolean matchRole(UserRole[] expected, UserRole actual) {
        for (UserRole role : expected) {
            if (role == actual) {
                return true;
            }
        }
        return false;
    }

    private String[] roleCodes(UserRole[] roles) {
        String[] codes = new String[roles.length];
        for (int i = 0; i < roles.length; i++) {
            codes[i] = roles[i].getCode();
        }
        return codes;
    }
}

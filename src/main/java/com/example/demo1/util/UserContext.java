package com.example.demo1.util;

import com.example.demo1.entity.User;
import com.example.demo1.enums.UserRole;

/**
 * 当前登录用户上下文（ThreadLocal，请求结束后由拦截器清理）
 */
public final class UserContext {

    private static final ThreadLocal<LoginUser> HOLDER = new ThreadLocal<>();

    private UserContext() {
    }

    public static void set(LoginUser loginUser) {
        HOLDER.set(loginUser);
    }

    public static LoginUser get() {
        return HOLDER.get();
    }

    public static LoginUser require() {
        LoginUser loginUser = HOLDER.get();
        if (loginUser == null) {
            throw new IllegalStateException("当前线程无登录用户信息");
        }
        return loginUser;
    }

    public static void clear() {
        HOLDER.remove();
    }

    public record LoginUser(Long id, String username, UserRole role) {

        public static LoginUser of(User user) {
            return new LoginUser(user.getId(), user.getUsername(), user.getRole());
        }

        public boolean isAdmin() {
            return role == UserRole.ADMIN;
        }
    }
}

package com.example.demo1.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.example.demo1.dto.LoginRequest;
import com.example.demo1.dto.LoginVO;
import com.example.demo1.dto.RegisterRequest;
import com.example.demo1.dto.UserVO;
import com.example.demo1.entity.User;

import java.util.List;

public interface UserService extends IService<User> {

    /**
     * 注册：用户名唯一校验 + BCrypt 加密存储
     */
    UserVO register(RegisterRequest request);

    /**
     * 登录：校验账号密码后签发 JWT
     */
    LoginVO login(LoginRequest request);

    /**
     * 根据用户名查询未被逻辑删除的用户
     */
    User findByUsername(String username);

    /**
     * 查询用户列表（含逻辑删除过滤）
     */
    List<User> listAll();

    /**
     * 启用 / 禁用用户
     */
    void updateStatus(Long id, Integer status);
}

package com.example.demo1.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.example.demo1.common.BusinessException;
import com.example.demo1.dto.LoginRequest;
import com.example.demo1.dto.LoginVO;
import com.example.demo1.dto.RegisterRequest;
import com.example.demo1.dto.UserVO;
import com.example.demo1.entity.User;
import com.example.demo1.enums.UserRole;
import com.example.demo1.mapper.UserMapper;
import com.example.demo1.service.UserService;
import com.example.demo1.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserServiceImpl(PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserVO register(RegisterRequest request) {
        if (findByUsername(request.username()) != null) {
            throw new BusinessException("用户名已存在");
        }
        User user = new User();
        user.setUsername(request.username());
        // 明文密码 + BCrypt 加盐哈希
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setNickname(request.nickname() == null || request.nickname().isBlank()
                ? request.username() : request.nickname());
        user.setEmail(request.email());
        user.setPhone(request.phone());
        user.setRole(UserRole.USER);
        user.setStatus(1);
        save(user);
        return UserVO.from(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginVO login(LoginRequest request) {
        User user = findByUsername(request.username());
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }
        if (Integer.valueOf(0).equals(user.getStatus())) {
            throw BusinessException.forbidden("账号已被禁用，请联系管理员");
        }
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        // 记录最近登录时间
        lambdaUpdate()
                .eq(User::getId, user.getId())
                .set(User::getLastLoginTime, LocalDateTime.now())
                .update();
        user.setLastLoginTime(LocalDateTime.now());

        Map<String, Object> claims = new HashMap<>(4);
        claims.put("username", user.getUsername());
        claims.put("role", user.getRole().getCode());
        String token = jwtUtil.generateToken(String.valueOf(user.getId()), claims);

        return new LoginVO(token, "Bearer", jwtUtil.getExpirationSeconds(), UserVO.from(user));
    }

    @Override
    public User findByUsername(String username) {
        // 逻辑删除由 MyBatis-Plus 自动追加 deleted = 0 条件
        return lambdaQuery().eq(User::getUsername, username).one();
    }

    @Override
    public List<User> listAll() {
        return lambdaQuery().orderByDesc(User::getCreateTime).list();
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        if (!Integer.valueOf(0).equals(status) && !Integer.valueOf(1).equals(status)) {
            throw new BusinessException("status 只能是 0 或 1");
        }
        User user = getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setStatus(status);
        updateById(user);
    }
}

DROP TABLE IF EXISTS sys_user;

CREATE TABLE sys_user
(
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    username        VARCHAR(50)  NOT NULL COMMENT '用户名',
    password        VARCHAR(100) NOT NULL COMMENT '密码(BCrypt密文)',
    nickname        VARCHAR(50)  NOT NULL COMMENT '昵称',
    email           VARCHAR(100) COMMENT '邮箱',
    phone           VARCHAR(20)  COMMENT '手机号',
    role            VARCHAR(20)  NOT NULL DEFAULT 'USER' COMMENT '角色 USER/ADMIN',
    status          TINYINT      NOT NULL DEFAULT 1 COMMENT '状态 1-正常 0-禁用',
    last_login_time DATETIME COMMENT '最近登录时间',
    create_time     DATETIME COMMENT '创建时间',
    update_time     DATETIME COMMENT '更新时间',
    deleted         TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除'
);

CREATE UNIQUE INDEX uk_sys_user_username ON sys_user (username);

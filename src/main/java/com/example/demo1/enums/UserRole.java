package com.example.demo1.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 用户角色
 */
public enum UserRole {

    USER("USER", "普通用户"),
    ADMIN("ADMIN", "管理员");

    @JsonValue
    private final String code;

    private final String desc;

    UserRole(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static boolean contains(String code) {
        for (UserRole role : values()) {
            if (role.code.equals(code)) {
                return true;
            }
        }
        return false;
    }
}

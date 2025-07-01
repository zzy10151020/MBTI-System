package org.frostedstar.mbtisystem.model;

import lombok.Getter;

/**
 * 用户角色枚举
 */
@Getter
public enum UserRole {
    /**
     * 普通用户
     */
    USER("user"),
    
    /**
     * 管理员
     */
    ADMIN("admin");
    
    private final String value;
    
    UserRole(String value) {
        this.value = value;
    }

    /**
     * 根据字符串值获取枚举
     */
    public static UserRole fromValue(String value) {
        for (UserRole role : UserRole.values()) {
            if (role.getValue().equalsIgnoreCase(value)) {
                return role;
            }
        }
        throw new IllegalArgumentException("No enum constant " + UserRole.class.getName() + "." + value);
    }
    
    @Override
    public String toString() {
        return value;
    }
}

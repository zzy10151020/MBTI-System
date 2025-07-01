package org.frostedstar.mbtisystem.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * UserRole枚举与数据库字符串值的转换器
 */
@Converter(autoApply = true)
public class UserRoleConverter implements AttributeConverter<UserRole, String> {

    @Override
    public String convertToDatabaseColumn(UserRole role) {
        if (role == null) {
            return null;
        }
        return role.getValue();
    }

    @Override
    public UserRole convertToEntityAttribute(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return UserRole.fromValue(value);
    }
}

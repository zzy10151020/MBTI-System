package org.frostedstar.mbtisystem.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * MbtiDimension枚举与数据库字符串值的转换器
 */
@Converter(autoApply = true)
public class MbtiDimensionConverter implements AttributeConverter<MbtiDimension, String> {

    @Override
    public String convertToDatabaseColumn(MbtiDimension dimension) {
        if (dimension == null) {
            return null;
        }
        return dimension.getValue();
    }

    @Override
    public MbtiDimension convertToEntityAttribute(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return MbtiDimension.fromValue(value);
    }
}

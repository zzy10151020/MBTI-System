package org.frostedstar.mbtisystem.model;

/**
 * MBTI 人格维度枚举
 * E/I: 外向/内向
 * S/N: 感觉/直觉
 * T/F: 思考/情感
 * J/P: 判断/知觉
 */
public enum MbtiDimension {
    /**
     * 外向/内向维度
     */
    EI("E/I"),
    
    /**
     * 感觉/直觉维度
     */
    SN("S/N"),
    
    /**
     * 思考/情感维度
     */
    TF("T/F"),
    
    /**
     * 判断/知觉维度
     */
    JP("J/P");
    
    private final String value;
    
    MbtiDimension(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    public static MbtiDimension fromValue(String value) {
        for (MbtiDimension dimension : MbtiDimension.values()) {
            if (dimension.value.equals(value)) {
                return dimension;
            }
        }
        throw new IllegalArgumentException("Unknown dimension: " + value);
    }
}

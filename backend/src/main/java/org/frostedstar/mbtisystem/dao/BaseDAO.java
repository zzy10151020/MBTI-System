package org.frostedstar.mbtisystem.dao;

import java.util.List;
import java.util.Optional;

/**
 * 基础 DAO 接口
 */
public interface BaseDAO<T, ID> {
    
    /**
     * 保存实体
     */
    T save(T entity);
    
    /**
     * 根据 ID 查找实体
     */
    Optional<T> findById(ID id);
    
    /**
     * 查找所有实体
     */
    List<T> findAll();
    
    /**
     * 更新实体
     */
    boolean update(T entity);
    
    /**
     * 根据 ID 删除实体
     */
    boolean deleteById(ID id);
    
    /**
     * 统计数量
     */
    long count();
}

package org.frostedstar.mbtisystem.dao;

import org.frostedstar.mbtisystem.entity.Option;

import java.util.List;

/**
 * 选项 DAO 接口
 */
public interface OptionDAO extends BaseDAO<Option, Integer> {
    
    /**
     * 根据问题ID查找选项
     */
    List<Option> findByQuestionId(Integer questionId);
    
    /**
     * 根据问题ID删除所有选项
     */
    boolean deleteByQuestionId(Integer questionId);
    
    /**
     * 批量保存选项
     */
    List<Option> saveBatch(List<Option> options);
}

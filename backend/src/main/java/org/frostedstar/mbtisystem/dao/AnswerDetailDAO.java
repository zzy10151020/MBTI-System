package org.frostedstar.mbtisystem.dao;

import org.frostedstar.mbtisystem.entity.AnswerDetail;

import java.util.List;

/**
 * 回答详情 DAO 接口
 */
public interface AnswerDetailDAO extends BaseDAO<AnswerDetail, Integer> {
    
    /**
     * 根据回答ID查找回答详情
     */
    List<AnswerDetail> findByAnswerId(Integer answerId);
    
    /**
     * 根据回答ID删除所有回答详情
     */
    boolean deleteByAnswerId(Integer answerId);
    
    /**
     * 批量保存回答详情
     */
    List<AnswerDetail> saveBatch(List<AnswerDetail> answerDetails);
}

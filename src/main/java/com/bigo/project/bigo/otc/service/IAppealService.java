package com.bigo.project.bigo.otc.service;

import com.bigo.project.bigo.otc.entity.AppealEntity;

import java.util.List;

/**
 * @description:
 * @author: wenxm
 * @date: 2020/7/22 15:00
 */
public interface IAppealService {

    /**
     * 插入
     * @param appeal
     * @return
     */
    int insert(AppealEntity appeal);

    /**
     * 更新
     * @param appeal
     * @return
     */
    int update(AppealEntity appeal);

    /**
     * 根据ID获取
     * @param id
     * @return
     */
    AppealEntity getById(Long id);

    /**
     * 查询列表
     * @param entity
     * @return
     */
    List<AppealEntity> listByEntity(AppealEntity entity);

    /**
     * 通过申诉
     * @param entity
     * @return
     */
    Boolean passAppeal(AppealEntity entity);

    /**
     * 驳回申诉
     * @param entity
     * @return
     */
    Boolean rejectAppeal(AppealEntity entity);
}

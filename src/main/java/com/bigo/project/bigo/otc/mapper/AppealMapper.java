package com.bigo.project.bigo.otc.mapper;

import com.bigo.project.bigo.otc.domain.Payment;
import com.bigo.project.bigo.otc.entity.AppealEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @description: 支付方式mapper
 * @author: wenxm
 * @date: 2020/7/20 15:28
 */
public interface AppealMapper {

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
}

package com.bigo.project.bigo.userinfo.service;

import com.bigo.project.bigo.userinfo.domain.UserLevel;

import java.math.BigDecimal;

/**
 * @description: 用户等级服务
 * @author: wenxm
 * @date: 2020/6/29 18:13
 */
public interface IUserLevelService {

    /**
     * 获取等级信息
     * @param uid
     * @return
     */
    UserLevel getByUid(Long uid);

    /**
     * 获取等级信息
     * @param level
     * @return
     */
    UserLevel getByLevel(Integer level);

    /**
     * 根据等级获取合约手续费比例
     * @param level
     * @return
     */
    BigDecimal getFeeByLevel(Integer level);

    /**
     * 根据等级获取一级分佣比例
     * @param level
     * @return
     */
    BigDecimal getFirstRateByLevel(Integer level);

    /**
     * 根据等级获取二级分佣比例
     * @param level
     * @return
     */
    BigDecimal getSecondRateByLevel(Integer level);

    /**
     * 获取用户合约手续费比例
     * @param uid
     * @return
     */
    BigDecimal getFeeByUid(Long uid);

    /**
     * 获取用户一级分佣比例
     * @param uid
     * @return
     */
    BigDecimal getFirstRateByUid(Long uid);

    /**
     * 获取用户二级分佣比例
     * @param uid
     * @return
     */
    BigDecimal getSecondRateByUid(Long uid);




}

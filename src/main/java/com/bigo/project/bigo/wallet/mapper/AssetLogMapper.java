package com.bigo.project.bigo.wallet.mapper;

import com.bigo.project.bigo.wallet.domain.AssetChange;
import com.bigo.project.bigo.wallet.domain.AssetLog;
import com.bigo.project.bigo.wallet.entity.AssetLogEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Description 资产变更记录mapper
 * @Author wenxm
 * @Date 2020/6/16 10:58
 */
public interface AssetLogMapper {

    /**
     * 新增资产变更记录
     * @param assertLog
     * @return
     */
    Long insertLog(AssetLog assertLog);

    /**
     * 批量插入
     * @param list
     * @return
     */
    int batchInsert(List<AssetLog> list);

    /**
     * 获取符合条件的记录列表
     * @param param
     * @return
     */
    List<AssetLog> listByParam(AssetLog param);

    /**
     * 获取符合条件的记录列表
     * @param param
     * @return
     */
    List<AssetLogEntity> listByEntity(AssetLogEntity param);

    Long getExchangeNum(Map params);

    BigDecimal getExchangeCount(Map params);

    List<AssetLogEntity> libraFrozenList(AssetLogEntity entity);

    void updateReleaseStatus(AssetLogEntity assetLogEntity);

    int saveLog(AssetLog log);


    BigDecimal getTeamCommission(AssetLogEntity entity);
}

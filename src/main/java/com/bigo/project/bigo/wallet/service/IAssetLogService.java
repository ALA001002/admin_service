package com.bigo.project.bigo.wallet.service;

import com.bigo.project.bigo.api.vo.AssetLogVO;
import com.bigo.project.bigo.api.vo.ProfitVO;
import com.bigo.project.bigo.enums.AssetLogSubTypeEnum;
import com.bigo.project.bigo.enums.AssetLogTypeEnum;
import com.bigo.project.bigo.wallet.domain.AssetLog;
import com.bigo.project.bigo.wallet.domain.Wallet;
import com.bigo.project.bigo.wallet.entity.AssetLogEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Description 资产变更service
 * @Author wenxm
 * @Date 2020/6/21 15:49
 */
public interface IAssetLogService {

    /**
     * 插入资产变更记录
     * @param wallet
     * @param amount
     * @param dim 变更维度，0-增 1-减
     * @param type
     * @param subType
     * @return
     */
    Long insertAssetLog(Wallet wallet, BigDecimal amount, Integer dim ,AssetLogTypeEnum type, AssetLogSubTypeEnum subType);

    /**
     * 插入分佣变更记录
     * @param wallet
     * @param amount
     * @param dim 变更维度，0-增 1-减
     * @param type
     * @param subType
     * @param contractId 获取分佣的合约id
     * @return
     */
    Long insertAssetLog(Wallet wallet, BigDecimal amount, Integer dim ,AssetLogTypeEnum type, AssetLogSubTypeEnum subType, Long contractId);

    /**
     * 获取分佣信息
     * @param uid 用户id`
     * @param coin 币种
     * @return
     */
    ProfitVO getProfitInfo(Long uid, String coin);

    /**
     * 获取对应币种的资金变更记录
     * @param uid 用户id
     * @param coin 币种
     * @return
     */
    List<AssetLogVO> listAssetLogByCoin(Long uid, String coin);

    /**
     * 获取资金变更记录
     * @param assetLog 查询参数
     * @return
     */
    List<AssetLogEntity> listAssetLogByEntity(AssetLogEntity assetLog);

    List<AssetLogVO> assetLogCoinList(Long uid, String coin, Integer type,Integer subType, int dim);

    Long getExchangeNum(Map params);

    BigDecimal getExchangeCount(Map params);

    List<AssetLogEntity> libraFrozenList(AssetLogEntity entity);

    void release(AssetLogEntity assetLogEntity);

    int saveAssetLog(Wallet wallet, BigDecimal amount, Integer dim ,AssetLogTypeEnum type, AssetLogSubTypeEnum subType, Long contractId);

    BigDecimal getTeamCommission(AssetLogEntity entity);
}

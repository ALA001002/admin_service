package com.bigo.project.bigo.wallet.service.impl;

import com.bigo.common.utils.CoinUtils;
import com.bigo.common.utils.DateUtils;
import com.bigo.common.utils.DictUtils;
import com.bigo.common.utils.StringUtils;
import com.bigo.project.bigo.api.vo.AssetLogVO;
import com.bigo.project.bigo.api.vo.ProfitVO;
import com.bigo.project.bigo.enums.*;
import com.bigo.project.bigo.wallet.domain.AssetChange;
import com.bigo.project.bigo.wallet.domain.AssetLog;
import com.bigo.project.bigo.wallet.domain.Wallet;
import com.bigo.project.bigo.wallet.entity.AssetLogEntity;
import com.bigo.project.bigo.wallet.entity.WithdrawEntity;
import com.bigo.project.bigo.wallet.mapper.AssetLogMapper;
import com.bigo.project.bigo.wallet.service.IAssetLogService;
import com.bigo.project.bigo.wallet.service.IWalletService;
import com.google.common.collect.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * @Description
 * @Author wenxm
 * @Date 2020/6/21 16:08
 */
@Service
public class AssetLogServiceImpl implements IAssetLogService {

    @Autowired
    private AssetLogMapper assetLogMapper;

    @Autowired
    private IWalletService walletService;

    @Override
    public Long insertAssetLog(Wallet wallet, BigDecimal amount, Integer dim, AssetLogTypeEnum type, AssetLogSubTypeEnum subType) {
        AssetLog log = new AssetLog();
        log.setWalletId(wallet.getId());
        log.setType(type.getType());
        log.setSubType(subType == null ? null : subType.getType());
        log.setDim(dim);
        log.setBefore(wallet.getBalance());
        log.setAmount(amount);
        if(dim == 0) {
            log.setAfter(wallet.getBalance().add(amount));
        }else{
            log.setAfter(wallet.getBalance().subtract(amount));
        }
        return assetLogMapper.insertLog(log);
    }

    @Override
    public Long insertAssetLog(Wallet wallet, BigDecimal amount, Integer dim, AssetLogTypeEnum type, AssetLogSubTypeEnum subType, Long contractId) {
        AssetLog log = new AssetLog();
        log.setWalletId(wallet.getId());
        log.setType(type.getType());
        log.setSubType(subType == null ? null : subType.getType());
        log.setContractId(contractId);
        log.setDim(dim);
        log.setBefore(wallet.getBalance());
        log.setAmount(amount);
        if(subType == AssetLogSubTypeEnum.FROZEN) {
            String countdownTime = DictUtils.getDictValue("bigo_base_config", "countdown_time");
            log.setReleaseTime(DateUtils.dateTime(DateUtils.YYYY_MM_DD_HH_MM_SS, countdownTime));
//            Integer releaseDayNum = CoinUtils.getReleaseDayNum();
//            log.setReleaseTime(DateUtils.getEndTime(new Date(), releaseDayNum));
        }
        if(dim == 0) {
            log.setAfter(wallet.getBalance().add(amount));
        }else{
            log.setAfter(wallet.getBalance().subtract(amount));
        }
        return assetLogMapper.insertLog(log);
    }

    public static void main(String[] args) {
        System.out.println(DateUtils.dateTime(DateUtils.YYYY_MM_DD_HH_MM_SS, "2020-07-11 00:00:00"));
    }

    @Override
    public ProfitVO getProfitInfo(Long uid, String coin) {
        AssetLog param = new AssetLog();
        param.setUid(uid);
        param.setCoin(coin);
        param.setType(AssetLogTypeEnum.RAKE_BACK.getType());
        List<AssetLog> logList = assetLogMapper.listByParam(param);
        BigDecimal yesterdayProfit = BigDecimal.ZERO;
        BigDecimal totalProfit = BigDecimal.ZERO;
        for(AssetLog log : logList){
            if(DateUtils.isYesterday(log.getOperateTime())){
                yesterdayProfit = yesterdayProfit.add(log.getAmount());
            }
            totalProfit = totalProfit.add(log.getAmount());
        }
        ProfitVO profitVO = new ProfitVO();
        profitVO.setCoin(coin);
        profitVO.setYesterdayProfit(yesterdayProfit);
        profitVO.setTotalProfit(totalProfit);
        return profitVO;
    }

    @Override
    public List<AssetLogVO> listAssetLogByCoin(Long uid, String coin) {
        AssetLog param = new AssetLog();
        param.setUid(uid);
        if(StringUtils.isNotEmpty(coin) && !"undefined".equals(coin)) param.setCoin(coin);
        List<AssetLog> assetLogList = assetLogMapper.listByParam(param);
        int size = assetLogList.size();
        List<AssetLogVO> resultList = new ArrayList<>(size);
        for(int i = 0; i < size; i++){
            AssetLogVO log = new AssetLogVO();
            BeanUtils.copyProperties(assetLogList.get(i), log);
            resultList.add(log);
        }
        return resultList;
    }

    @Override
    public List<AssetLogEntity> listAssetLogByEntity(AssetLogEntity assetLog) {
        return assetLogMapper.listByEntity(assetLog);
    }

    @Override
    public List<AssetLogVO> assetLogCoinList(Long uid, String coin, Integer type, Integer subType, int dim) {
        List<AssetLogVO> voList = new ArrayList<>();
        AssetLogEntity assetLog = new AssetLogEntity();
        assetLog.setUid(uid);
        assetLog.setCoin(coin);
        assetLog.setType(type);
        assetLog.setSubType(subType);
        assetLog.setDim(dim);
        List<AssetLogEntity> logList = assetLogMapper.listByEntity(assetLog);
        logList.forEach(item ->{
            AssetLogVO vo = new AssetLogVO();
            BeanUtils.copyProperties(item, vo);
            voList.add(vo);
        });
        return voList;
    }

    @Override
    public Long getExchangeNum(Map params) {
        return assetLogMapper.getExchangeNum(params);
    }

    @Override
    public BigDecimal getExchangeCount(Map params) {
        return assetLogMapper.getExchangeCount(params);
    }

    /**
     * 获取 冻结中，即将解冻的记录
     * @return
     */
    @Override
    public List<AssetLogEntity> libraFrozenList(AssetLogEntity entity) {
        return assetLogMapper.libraFrozenList(entity);
    }

    /**
     * 释放冻结libra币
     * @param assetLogEntity
     */
    @Override
    @Transactional
    public void release(AssetLogEntity assetLogEntity) {
        // 释放
        AssetChange change = AssetChange.builder().uid(assetLogEntity.getUid())
                .currency(assetLogEntity.getCoin())
                .dim(0)
                .type(AssetLogTypeEnum.RELEASE)
                .subType(AssetLogSubTypeEnum.RELEASE)
                .walletType(WalletTypeEnum.CAPITAL_ACCOUNT.getType())
                .amount(assetLogEntity.getAmount())
                .amountType(AmountTypeEnum.BANLANCE.getType())
                .walletId(assetLogEntity.getWalletId())
                .build();
        walletService.changeAsset(change);
        // 修改记录为已释放
        assetLogMapper.updateReleaseStatus(assetLogEntity);
    }

    @Override
    public int saveAssetLog(Wallet wallet, BigDecimal amount, Integer dim, AssetLogTypeEnum type, AssetLogSubTypeEnum subType, Long contractId) {
        AssetLog log = new AssetLog();
        log.setWalletId(wallet.getId());
        log.setType(type.getType());
        log.setSubType(subType == null ? null : subType.getType());
        log.setContractId(contractId);
        log.setDim(dim);
        log.setBefore(wallet.getBalance());
        log.setAmount(amount);
        if(subType == AssetLogSubTypeEnum.FROZEN) {
            String countdownTime = DictUtils.getDictValue("bigo_base_config", "countdown_time");
            log.setReleaseTime(DateUtils.dateTime(DateUtils.YYYY_MM_DD_HH_MM_SS, countdownTime));
//            Integer releaseDayNum = CoinUtils.getReleaseDayNum();
//            log.setReleaseTime(DateUtils.getEndTime(new Date(), releaseDayNum));
        }
        if(dim == 0) {
            log.setAfter(wallet.getBalance().add(amount));
        }else{
            log.setAfter(wallet.getBalance().subtract(amount));
        }
        return assetLogMapper.saveLog(log);
    }

    @Override
    public BigDecimal getTeamCommission(AssetLogEntity entity) {
        return assetLogMapper.getTeamCommission(entity);
    }


}

package com.bigo.project.bigo.wallet.service;

import com.bigo.framework.web.domain.AjaxResult;
import com.bigo.project.bigo.api.domain.AssetTransferParam;
import com.bigo.project.bigo.api.domain.CoinExchange;
import com.bigo.project.bigo.api.vo.AccountVO;
import com.bigo.project.bigo.enums.AssetLogSubTypeEnum;
import com.bigo.project.bigo.enums.AssetLogTypeEnum;
import com.bigo.project.bigo.userinfo.domain.BigoUser;
import com.bigo.project.bigo.userinfo.entity.BigoUserEntity;
import com.bigo.project.bigo.wallet.domain.AssetChange;
import com.bigo.project.bigo.wallet.domain.Wallet;
import com.bigo.project.bigo.wallet.domain.WalletAddress;
import com.bigo.project.bigo.wallet.domain.Withdraw;
import com.bigo.project.bigo.wallet.entity.WalletEntity;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Description 钱包service
 * @Author wenxm
 * @Date 2020/6/20 10:02
 */
public interface IWalletService {

    /**
     * 新增钱包
     * @param uid
     * @return
     */
    int addWallet(Long uid);

    int addWallet(Wallet wallet);

    /**
     * 资金划转
     * @param transferParam 资金划转参数
     * @return
     */
    Boolean transferAsset(AssetTransferParam transferParam);

    /**
     * 获取账户列表
     * @param uid
     * @return
     */
    List<AccountVO> listAccount(Long uid);

    /**
     * 资产变更
     * @param change
     * @return
     */
    Boolean changeAsset(AssetChange change);

    /**
     * 分佣资产变更
     * @param change
     * @param contractId 获取分佣的合约id
     * @return
     */
    Boolean changeAsset(AssetChange change, Long contractId);

    /**
     * 获取钱包地址
     * @param address
     * @return
     */
    String getWalletAddress(WalletAddress address);

    /**
     * 闪兑
     * @param exchange 闪兑参数
     * @return
     */
    Boolean quickExchange(CoinExchange exchange);

    /**
     * 获取钱包列表
     * @param entity
     * @return
     */
    List<WalletEntity> listByEntity(WalletEntity entity);

    /**
     *  判断用户钱包是否有钱
     * @param uids 用户uid，用‘,’分隔
     * @return 有钱的用户的数量
     */
    Integer countUserHasMoney(String uids);

    /**
     * 根据id获取钱包
     * @param id
     * @return
     */
    Wallet getById(Long id);


    Wallet getWallet(Wallet paramsWallet);

    void addBalance(BigoUser user, WalletEntity entity, Wallet wallet);

    List<Wallet> listForzenWallet(Wallet entity);

    void release(Wallet wallet);

    /**
     * 余额变更，乐观锁
     */
    void lockChange(BigDecimal amount, String currencyCode, Long userId, int type, int dim, AssetLogTypeEnum assetLogTypeEnum);

    void lockChange(BigDecimal amount, String currencyCode, Long userId, int walletType,int dim, AssetLogTypeEnum assetLogTypeEnum, AssetLogSubTypeEnum assetLogSubTypeEnum);

    void complementWallet(BigoUserEntity userEntity);

    List<Map> countUserInfo(List<Long> uidList,Integer authStatus);

    void converts(Wallet wallet);

    void releaseCurrency();
}

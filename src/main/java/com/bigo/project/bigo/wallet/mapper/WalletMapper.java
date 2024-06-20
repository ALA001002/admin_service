package com.bigo.project.bigo.wallet.mapper;

import com.bigo.project.bigo.api.vo.AccountVO;
import com.bigo.project.bigo.wallet.domain.AssetChange;
import com.bigo.project.bigo.wallet.domain.Wallet;
import com.bigo.project.bigo.wallet.entity.WalletEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Description 用户钱包mapper
 * @Author wenxm
 * @Date 2020/6/16 10:58
 */
public interface WalletMapper {

    /**
     * 新增钱包
     * @param wallet
     * @return
     */
    Long insertWallet(Wallet wallet);

    /**
     * 批量插入
     * @param list
     * @return
     */
    int batchInsert(List<Wallet> list);

    /**
     * 增加用户钱包余额
     * @param change
     * @return 影响行数
     */
    int addBalance(AssetChange change);

    /**
     * 扣除用户钱包余额
     * @param change
     * @return 影响行数
     */
    int subBalance(AssetChange change);

    /**
     * 根据uid和币种获取钱包信息
     * @param wallet 查询参数
     * @return
     */
    List<AccountVO> listWallet(Wallet wallet);

    /**
     * 更新查询
     * @param wallet 钱包信息
     * @return
     */
    Wallet getForUpdate(Wallet wallet);

    /**
     * 根据参数获取列表
     * @param walletEntity
     * @return
     */
    List<WalletEntity> listByEntity(WalletEntity walletEntity);

    /**
     *  判断用户钱包是否有钱
     * @param uids 用户uid，用‘,’分隔
     * @return 有钱的用户的数量
     */
    Integer countUserHasMoney(@Param("uids") String uids);

    /**
     * 根据id获取钱包
     * @param id
     * @return
     */
    Wallet getById(Long id);


    int addFrozen(AssetChange change);

    int subFrozen(AssetChange change);

    Wallet getByUserWallet(Wallet wallet);

    Wallet getWallet(Wallet wallet);

    List<Wallet> listForzenWallet(Wallet entity);

    int changeLockFrozen(Wallet wallet);

    int changeLockBalance(Wallet wallet);

    List<Map> countUserInfo(@Param("uidList") List<Long> uidList, @Param("authStatus") Integer authStatus);

    void converts(Wallet wallet);

    WalletEntity releaseWallet(WalletEntity param);
    List<WalletEntity> releaseWalletList(WalletEntity param);

}

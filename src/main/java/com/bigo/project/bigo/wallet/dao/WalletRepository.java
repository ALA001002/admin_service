
package com.bigo.project.bigo.wallet.dao;

import com.bigo.project.bigo.wallet.jpaEntity.BgWallet;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

public interface WalletRepository extends BaseRepository<BgWallet> {
    @Query(value = "select uid userId,currency coin,sum(balance) value from bg_wallet where uid in :userIds and type=0 group by uid,currency",nativeQuery = true)
    List<Map> countUserInfo(@Param("userIds")List<Long> userIds);

//    @Query(value = "select uid userId,currency coin,sum(balance) value from bg_wallet where type=0 group by uid,currency",nativeQuery = true)
//    List<Map> countUserInfoWithoutUserIds();
//
//    @Query(value = "SELECT A.email,A.top_uid parentUid,A.uid userId from bg_user A where A.uid in :userIds",nativeQuery = true)
//    List<Map> queryUserInfo(@Param("userIds")List<Long> userIds);
//
//    @Query(value = "SELECT A.email,A.top_uid parentUid,A.uid userId from bg_user A",nativeQuery = true)
//    List<Map> queryUserInfoWithdrawUserIds();
//
//    @Query(value = "select uid from bg_wallet where uid not in (select uid from bg_wallet where currency='USDT_TRC20' GROUP BY uid) group by uid",nativeQuery = true)
//    List<Integer> findInitUser();
}

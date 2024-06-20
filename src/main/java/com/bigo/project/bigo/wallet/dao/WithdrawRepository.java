
package com.bigo.project.bigo.wallet.dao;

import com.bigo.project.bigo.wallet.jpaEntity.TWithdraw;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

public interface WithdrawRepository extends BaseRepository<TWithdraw> {
  /*  @Query(value = "select uid userId,coin,type,sum(money) value from (select D.uid,if(D.coin='USDT_TRC20','USDT',D.coin) coin,D.money,D.type type from t_withdraw D,bg_user B where D.uid in :userIds and D.uid=B.uid and D.check_status=1 and D.status=1 and B.status!=2) C group by uid,coin,type",nativeQuery = true)
    List<Map> countUserInfo(@Param("userIds")List<Long> userIds);

    @Query(value = "select uid userId,coin,type,sum(money) value from (select D.uid,if(D.coin='USDT_TRC20','USDT',D.coin) coin,D.money,D.type type from t_withdraw D,bg_user B where D.uid=B.uid and D.check_status=1 and D.status=1 and B.status!=2) C group by uid,coin,type",nativeQuery = true)
    List<Map> countUserInfoWithoutUserids();



    @Query(value = "select A.type,sum(A.money) money from t_withdraw A,bg_user B where A.uid=B.uid and A.uid=:uid and A.check_status=1 and A.status=1 and B.status!=2 and if(A.coin='USDT_TRC20','USDT',A.coin)='USDT' group by A.type",nativeQuery = true)
    List<Map> withdrawDataByUId(@Param("uid")Long uid);

    @Query(value = "select A.type,sum(A.money) money from t_withdraw A,bg_user B where A.uid=B.uid and A.check_status=1 and A.status=1 and B.status!=2 and if(A.coin='USDT_TRC20','USDT',A.coin)='USDT' group by A.type",nativeQuery = true)
    List<Map> withdrawData();*/

    @Query(value = "select sum(money) value,A.coin,A.uid userId from (select C.money,if(C.coin='USDT_TRC20','USDT',C.coin) coin ,C.uid from t_withdraw C,bg_user B" +
            " where C.uid in :userIds  and C.uid=B.uid  and (C.type=4 or C.type=5)  and C.check_status=1 and C.status=1  and B.status!=2) A " +
            " group by A.uid,A.coin",nativeQuery = true)
    List<Map> recharge(@Param("userIds") List<Long> userIds);

    @Query(value = "select sum(money) value,A.coin,A.uid userId from (select C.money,if(C.coin='USDT_TRC20','USDT',C.coin) coin ,C.uid from t_withdraw C,bg_user B" +
            "             where C.uid in :userIds and C.uid=B.uid and (C.type=2 or C.type=3) and C.check_status=1 and C.status=1 and B.status!=2) A " +
            " group by A.uid,A.coin",nativeQuery = true)
    List<Map> withdraw(@Param("userIds") List<Long> userIds);
}

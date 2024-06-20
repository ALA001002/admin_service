
package com.bigo.project.bigo.config.dao;

import com.bigo.project.bigo.config.jpaEntity.EmailConfig;
import com.bigo.project.bigo.wallet.dao.BaseRepository;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

public interface EmailConfigRepository extends BaseRepository<EmailConfig> {
    @Query(value = "select * from bg_email_config where status=0 and template_no=3",nativeQuery = true)
    List<EmailConfig> selectStatusList();

    @Transactional
    @Modifying
    @Query(value = "update bg_email_config set send_count=send_count+1 where id=:id",nativeQuery = true)
    void updateEmailCount(@Param("id")Integer id);
//    @Query(value = "select uid userId,currency coin,sum(balance) value from bg_wallet where uid in :userIds and type=0 group by uid,currency",nativeQuery = true)
//    List<Map> countUserInfo(@Param("userIds")List<Long> userIds);


}

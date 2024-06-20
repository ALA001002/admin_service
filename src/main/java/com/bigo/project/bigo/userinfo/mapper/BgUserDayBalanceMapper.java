package com.bigo.project.bigo.userinfo.mapper;


import com.bigo.project.bigo.userinfo.entity.BgUserDayBalance;

import java.util.List;

/**
 * @description: 用户等级mapper
 * @author: wenxm
 * @date: 2020/6/29 18:06
 */
public interface BgUserDayBalanceMapper {


    int insertBalance(BgUserDayBalance balance);


    List<BgUserDayBalance> findByUserIdAndDayNo(BgUserDayBalance balance);

    List<BgUserDayBalance> findByUserId(Long userId);

    String findByUserIdToDay(Long uid);

    void updateToDayBalance(BgUserDayBalance bgUserDayBalance);
}

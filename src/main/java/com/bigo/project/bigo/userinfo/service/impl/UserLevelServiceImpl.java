package com.bigo.project.bigo.userinfo.service.impl;

import com.bigo.project.bigo.userinfo.domain.UserLevel;
import com.bigo.project.bigo.userinfo.mapper.UserLevelMapper;
import com.bigo.project.bigo.userinfo.service.IUserLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @description:
 * @author: wenxm
 * @date: 2020/6/29 18:16
 */
@Service
public class UserLevelServiceImpl implements IUserLevelService {

    @Autowired
    private UserLevelMapper userLevelMapper;

    @Override
    public UserLevel getByUid(Long uid) {
        return userLevelMapper.getByUid(uid);
    }

    @Override
    public UserLevel getByLevel(Integer level) {
        return userLevelMapper.getByLevel(level);
    }

    @Override
    public BigDecimal getFeeByLevel(Integer level) {
        UserLevel levelInfo = userLevelMapper.getByLevel(level);
        return levelInfo.getFee();
    }

    @Override
    public BigDecimal getFirstRateByLevel(Integer level) {
        UserLevel levelInfo = userLevelMapper.getByLevel(level);
        return levelInfo.getFirstRate();
    }

    @Override
    public BigDecimal getSecondRateByLevel(Integer level) {
        UserLevel levelInfo = userLevelMapper.getByLevel(level);
        return levelInfo.getSecondRate();
    }

    @Override
    public BigDecimal getFeeByUid(Long uid) {
        UserLevel levelInfo = userLevelMapper.getByUid(uid);
        return levelInfo.getFee();
    }

    @Override
    public BigDecimal getFirstRateByUid(Long uid) {
        UserLevel levelInfo = userLevelMapper.getByUid(uid);
        return levelInfo.getFirstRate();
    }

    @Override
    public BigDecimal getSecondRateByUid(Long uid) {
        UserLevel levelInfo = userLevelMapper.getByUid(uid);
        return levelInfo.getSecondRate();
    }
}

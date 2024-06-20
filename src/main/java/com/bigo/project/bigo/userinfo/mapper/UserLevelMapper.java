package com.bigo.project.bigo.userinfo.mapper;

import com.bigo.project.bigo.userinfo.domain.UserLevel;

/**
 * @description: 用户等级mapper
 * @author: wenxm
 * @date: 2020/6/29 18:06
 */
public interface UserLevelMapper {

    /**
     * 获取等级信息
     * @param level
     * @return
     */
    UserLevel getByLevel(Integer level);

    /**
     * 获取等级信息
     * @param uid
     * @return
     */
    UserLevel getByUid(Long uid);

}

package com.bigo.project.bigo.userinfo.service;

import com.bigo.project.bigo.api.vo.BigoUserVO;
import com.bigo.project.bigo.api.vo.ChildVO;
import com.bigo.project.bigo.userinfo.domain.BigoUser;
import com.bigo.project.bigo.userinfo.entity.BigoUserEntity;
import com.bigo.project.bigo.userinfo.entity.ExportBigoUserEntity;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Author wenxm
 * @Date 2020/6/17 16:46
 */
public interface IBigoUserService {

    /**
     * 用户注册
     * @param user
     * @return 用户UID
     */
    Long register(BigoUser user);

    /**
     * 更新用户
     * @param user
     * @return 影响行数
     */
    int updateUser(BigoUser user);

    /**
     * 手机号是否存在
     * @param phone 手机号
     * @return
     */
    Boolean isPhoneExist(String phone);

    /**
     * 邮箱是否存在
     * @param email 邮箱
     * @return
     */
    Boolean isEmailExist(String email);

    /**
     * 邀请码是否存在
     * @param inviteCode 邀请码
     * @return
     */
    Boolean isInviteCodeExist(Long inviteCode);

    /**
     * 根据uid获取用户信息
     * @param uid
     * @return
     */
    BigoUser getUserByUid(Long uid);

    /**
     * 根据手机号获取用户信息
     * @param phone
     * @return
     */
    BigoUser getUserByPhone(String phone);

    /**
     * 根据邮箱获取用户信息
     * @param email
     * @return
     */
    BigoUser getUserByEmail(String email);

    /**
     * 获取推荐人列表 （账号打码）
     * @param uidList
     * @return
     */
    List<ChildVO> listMaskChild(List<Long> uidList);

    /**
     * 获取用户信息列表
     * @param entity
     * @return
     */
    List<BigoUserEntity> listByEntity(BigoUserEntity entity);

    /**
     * 获取需要提升等级的用户，哪些用户可以提升等级：
     * 1）等级小于3
     * 2）已实名认证
     * @return
     */
    List<BigoUser> listNeedLevelUpUser();

    /**
     * 获取客服负责的用户列表
     * @param customerServiceId 客服id
     * @return
     */
    List<BigoUserEntity> listDockUserByCustomerServiceId(Long customerServiceId);

    Long getInvitedNumber(Map params);

    List<BigoUserEntity> getChildrenList(BigoUserEntity entity);

    List<String> getRechargeUser();

    List<Long> getLowerUids(Long parentUid);

    void updateSendEmailStatus(String email);

    List<BigoUser> listParentUids(Long uid);

    int editUser(BigoUser user);

    void updateUserParent(BigoUser updateUser,  BigoUser parentUser);

    List<BigoUserEntity> lowerListEntity(BigoUserEntity entity);

    void CheckAuthStatus(BigoUser updateUser);

    List<BigoUser> getParentUids();

    List<Long> getAuthUserCount(Long uid);

    List<BigoUserEntity> authListByEntity(BigoUserEntity entity);

    List<ExportBigoUserEntity> exportListByEntity(BigoUserEntity entity);
}

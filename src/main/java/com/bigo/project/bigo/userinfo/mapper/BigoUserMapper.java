package com.bigo.project.bigo.userinfo.mapper;

import com.bigo.project.bigo.userinfo.domain.BigoUser;
import com.bigo.project.bigo.userinfo.entity.BigoUserEntity;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Description 币高用户信息mapper
 * @Author wenxm
 * @Date 2020/6/16 10:58
 */
public interface BigoUserMapper {

    /**
     * 新增用户
     * @param bigoUser
     * @return
     */
    Long insertUser(BigoUser bigoUser);

    /**
     * 更新用户信息
     * @param bigoUser
     * @return 影响行数
     */
    int updateUser(BigoUser bigoUser);

    /**
     * 判断手机号是否存在
     * @param phone
     * @return
     */
    int checkPhoneUnique(String phone);

    /**
     * 判断邮箱是否存在
     * @param email
     * @return
     */
    int checkEmailUnique(String email);

    /**
     * 判断昵称是否存在
     * @param nickName
     * @return
     */
    int checkNickNameUnique(String nickName);

    /**
     * 判断邀请码是否存在
     * @param inviteCode
     * @return
     */
    int checkInviteCodeUnique(Long inviteCode);

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
     * 获取推荐人列表
     * @param list 推荐人uid列表
     * @return
     */
    List<BigoUser> listChild(List<Long> list);

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

    void editUserTopUid(Map map);

    List<BigoUserEntity> lowerListEntity(BigoUserEntity entity);

    List<BigoUser> getParentUids();

    List<Long> getAuthUserCount(@Param("uid") Long uid);

    List<BigoUserEntity> authListByEntity(BigoUserEntity entity);

    List<BigoUserEntity> teamListByEntity(BigoUserEntity entity);
}

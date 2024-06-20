package com.bigo.project.bigo.userinfo.service.impl;

import java.util.List;
import com.bigo.common.utils.DateUtils;
import com.bigo.project.bigo.userinfo.domain.BigoUser;
import com.bigo.project.bigo.userinfo.service.IBigoUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.bigo.project.bigo.userinfo.mapper.UserGoogleSecretMapper;
import com.bigo.project.bigo.userinfo.domain.UserGoogleSecret;
import com.bigo.project.bigo.userinfo.service.IUserGoogleSecretService;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户谷歌秘钥Service业务层处理
 * 
 * @author bigo
 * @date 2024-03-06
 */
@Service
public class UserGoogleSecretServiceImpl implements IUserGoogleSecretService 
{
    @Autowired
    private UserGoogleSecretMapper userGoogleSecretMapper;

    @Autowired
    private IBigoUserService bigoUserService;

    /**
     * 查询用户谷歌秘钥
     * 
     * @param id 用户谷歌秘钥ID
     * @return 用户谷歌秘钥
     */
    @Override
    public UserGoogleSecret selectUserGoogleSecretById(Long id)
    {
        return userGoogleSecretMapper.selectUserGoogleSecretById(id);
    }

    /**
     * 查询用户谷歌秘钥列表
     * 
     * @param userGoogleSecret 用户谷歌秘钥
     * @return 用户谷歌秘钥
     */
    @Override
    public List<UserGoogleSecret> selectUserGoogleSecretList(UserGoogleSecret userGoogleSecret)
    {
        return userGoogleSecretMapper.selectUserGoogleSecretList(userGoogleSecret);
    }

    /**
     * 新增用户谷歌秘钥
     * 
     * @param userGoogleSecret 用户谷歌秘钥
     * @return 结果
     */
    @Override
    public int insertUserGoogleSecret(UserGoogleSecret userGoogleSecret)
    {
        userGoogleSecret.setCreateTime(DateUtils.getNowDate());
        return userGoogleSecretMapper.insertUserGoogleSecret(userGoogleSecret);
    }

    /**
     * 修改用户谷歌秘钥
     * 
     * @param userGoogleSecret 用户谷歌秘钥
     * @return 结果
     */
    @Override
    public int updateUserGoogleSecret(UserGoogleSecret userGoogleSecret)
    {
        return userGoogleSecretMapper.updateUserGoogleSecret(userGoogleSecret);
    }

    /**
     * 批量删除用户谷歌秘钥
     * 
     * @param ids 需要删除的用户谷歌秘钥ID
     * @return 结果
     */
    @Override
    public int deleteUserGoogleSecretByIds(Long[] ids)
    {
        return userGoogleSecretMapper.deleteUserGoogleSecretByIds(ids);
    }

    /**
     * 删除用户谷歌秘钥信息
     * 
     * @param id 用户谷歌秘钥ID
     * @return 结果
     */
    @Override
    public int deleteUserGoogleSecretById(Long id)
    {
        return userGoogleSecretMapper.deleteUserGoogleSecretById(id);
    }

    @Override
    @Transactional
    public void unbundle(UserGoogleSecret userGoogleSecret) {
        //修改用户谷歌状态
        BigoUser updateUser = new BigoUser();
        updateUser.setUid(userGoogleSecret.getUid());
        updateUser.setGoogleSecretStatus(0);
        bigoUserService.updateUser(updateUser);
        //删除
        this.deleteUserGoogleSecretById(userGoogleSecret.getId());
    }
}

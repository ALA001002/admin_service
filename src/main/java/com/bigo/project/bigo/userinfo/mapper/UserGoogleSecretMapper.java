package com.bigo.project.bigo.userinfo.mapper;

import java.util.List;
import com.bigo.project.bigo.userinfo.domain.UserGoogleSecret;

/**
 * 用户谷歌秘钥Mapper接口
 * 
 * @author bigo
 * @date 2024-03-06
 */
public interface UserGoogleSecretMapper 
{
    /**
     * 查询用户谷歌秘钥
     * 
     * @param id 用户谷歌秘钥ID
     * @return 用户谷歌秘钥
     */
    public UserGoogleSecret selectUserGoogleSecretById(Long id);

    /**
     * 查询用户谷歌秘钥列表
     * 
     * @param userGoogleSecret 用户谷歌秘钥
     * @return 用户谷歌秘钥集合
     */
    public List<UserGoogleSecret> selectUserGoogleSecretList(UserGoogleSecret userGoogleSecret);

    /**
     * 新增用户谷歌秘钥
     * 
     * @param userGoogleSecret 用户谷歌秘钥
     * @return 结果
     */
    public int insertUserGoogleSecret(UserGoogleSecret userGoogleSecret);

    /**
     * 修改用户谷歌秘钥
     * 
     * @param userGoogleSecret 用户谷歌秘钥
     * @return 结果
     */
    public int updateUserGoogleSecret(UserGoogleSecret userGoogleSecret);

    /**
     * 删除用户谷歌秘钥
     * 
     * @param id 用户谷歌秘钥ID
     * @return 结果
     */
    public int deleteUserGoogleSecretById(Long id);

    /**
     * 批量删除用户谷歌秘钥
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteUserGoogleSecretByIds(Long[] ids);
}

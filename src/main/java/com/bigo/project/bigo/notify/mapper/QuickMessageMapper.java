package com.bigo.project.bigo.notify.mapper;

import com.bigo.project.bigo.notify.domain.QuickMessage;

import java.util.List;

public interface QuickMessageMapper {
    List<QuickMessage> list();

    /**
     * 查询快捷话术配置
     *
     * @param id 快捷话术配置ID
     * @return 快捷话术配置
     */
    public QuickMessage selectQuickMessageById(Long id);

    /**
     * 查询快捷话术配置列表
     *
     * @param quickMessage 快捷话术配置
     * @return 快捷话术配置集合
     */
    public List<QuickMessage> selectQuickMessageList(QuickMessage quickMessage);

    /**
     * 新增快捷话术配置
     *
     * @param quickMessage 快捷话术配置
     * @return 结果
     */
    public int insertQuickMessage(QuickMessage quickMessage);

    /**
     * 修改快捷话术配置
     *
     * @param quickMessage 快捷话术配置
     * @return 结果
     */
    public int updateQuickMessage(QuickMessage quickMessage);

    /**
     * 删除快捷话术配置
     *
     * @param id 快捷话术配置ID
     * @return 结果
     */
    public int deleteQuickMessageById(Long id);

    /**
     * 批量删除快捷话术配置
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteQuickMessageByIds(Long[] ids);
}

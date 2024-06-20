package com.bigo.project.bigo.marketsituation.service;

import com.bigo.project.bigo.marketsituation.entity.SlipDot;

import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: wenxm
 * @date: 2020/7/9 15:18
 */
public interface ISlipDotService {

    /**
     * 新增滑点
     * @param dot
     * @return
     */
    Boolean addSlipDot(SlipDot dot);

    /**
     * 删除滑点(逻辑删除)
     * @param id
     * @return
     */
    Boolean deleteLogical(Long id);

    /**
     * 开始滑点
     * @param dot
     * @return
     */
    Boolean startSlipDot(SlipDot dot);

    /**
     * 关闭滑点
     * @param dot
     * @return
     */
    Boolean stopSlipDot(SlipDot dot);

    /**
     * 获取滑点列表
     * @param dot
     * @return
     */
    List<SlipDot> listByEntity(SlipDot dot);

    /**
     * 根据id获取滑点信息
     * @param id
     * @return
     */
    SlipDot getById(Long id);

    /**
     * 查找时间段内的滑点
     * @param params
     * @return
     */
    List<SlipDot> listSlipDotByDate(Map<String,Object> params);
}

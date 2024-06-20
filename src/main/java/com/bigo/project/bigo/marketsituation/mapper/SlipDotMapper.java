package com.bigo.project.bigo.marketsituation.mapper;


import com.bigo.project.bigo.marketsituation.entity.DotRecord;
import com.bigo.project.bigo.marketsituation.entity.SlipDot;

import java.util.List;
import java.util.Map;

/**
 * @Description 滑点mapper
 * @Author wenxm
 * @Date 2020/6/19 15:31
 */
public interface SlipDotMapper {

    /**
     * 新增滑点
     * @param dot
     * @return
     */
    int insert(SlipDot dot);

    /**
     * 删除滑点(逻辑删除)
     * @param id
     * @return
     */
    Boolean deleteLogical(Long id);

    /**
     * 更新
     * @param dot
     * @return
     */
    int update(SlipDot dot);

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
     * 获取正在运行的滑点
     * @param symbol
     * @return
     */
    SlipDot getRunningDotBySymbol(String symbol);

    /**
     * 插入滑点操作记录
     * @param record
     * @return
     */
    int insertDotRecord(DotRecord record);

    /**
     * 查找时间段内的滑点
     * @param params
     * @return
     */
    List<SlipDot> listSlipDotByDate(Map<String,Object> params);
    
}

package com.bigo.project.bigo.contract.mapper;

import com.bigo.project.bigo.contract.domain.Frame;

import java.util.List;

/**
 * @description:
 * @author: wenxm
 * @date: 2020/7/5 20:22
 */
public interface FrameMapper {

    /**
     * 新增
     * @param frame
     * @return
     */
    int insert(Frame frame);

    /**
     * 获取插帧列表
     * @param frame
     * @return
     */
    List<Frame> listByParam(Frame frame);
}

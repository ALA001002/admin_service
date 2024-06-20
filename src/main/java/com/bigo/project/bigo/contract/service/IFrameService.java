package com.bigo.project.bigo.contract.service;

import com.bigo.project.bigo.contract.domain.Frame;

import java.util.List;

/**
 * @description:
 * @author: wenxm
 * @date: 2020/7/7 15:57
 */
public interface IFrameService {

    /**
     * 获取插帧列表
     * @param frame
     * @return
     */
    List<Frame> listByParam(Frame frame);
}

package com.bigo.project.bigo.contract.service.impl;

import com.bigo.project.bigo.contract.domain.Frame;
import com.bigo.project.bigo.contract.mapper.FrameMapper;
import com.bigo.project.bigo.contract.service.IFrameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description:
 * @author: wenxm
 * @date: 2020/7/7 15:58
 */
@Service
public class FrameServiceImpl implements IFrameService {

    @Autowired
    private FrameMapper frameMapper;

    @Override
    public List<Frame> listByParam(Frame frame) {
        return frameMapper.listByParam(frame);
    }
}

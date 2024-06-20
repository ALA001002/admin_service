package com.bigo.project.bigo.notify.NotifyinfoService;

import com.bigo.project.bigo.notify.domain.QuickMessage;
import com.bigo.project.bigo.notify.mapper.QuickMessageMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class QuickService {

    @Resource
    QuickMessageMapper quickMessageMapper;

    public List<QuickMessage> list(){
        return quickMessageMapper.list();
    }
}

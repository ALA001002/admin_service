package com.bigo.project.bigo.wallet.service;

import com.bigo.project.bigo.wallet.domain.PushData;

/**
 * @description:
 * @author: wenxm
 * @date: 2021/11/4 16:00
 */
public interface INotifyService {

    String notify(PushData pushData);

    String sendNotify(PushData pushData);
}

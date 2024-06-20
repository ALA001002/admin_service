package com.bigo.common.utils;

/**
 * @description:
 * @author: wenxm
 * @date: 2021/8/22 18:42
 */
public class SingletonLazy5 {

    private static volatile SingletonLazy5 singletonLazy;

    private SingletonLazy5() {

    }

    public static SingletonLazy5 getInstance() {
        try {
            if (null == singletonLazy) {
                // 模拟在创建对象之前做一些准备工作
                Thread.sleep(1000);
                synchronized (SingletonLazy5.class) {
                    if(null == singletonLazy) {
                        singletonLazy = new SingletonLazy5();
                    }
                }
            }
        } catch (InterruptedException e) {
            // TODO: handle exception
        }
        return singletonLazy;
    }
}

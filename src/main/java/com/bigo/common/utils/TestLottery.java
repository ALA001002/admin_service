package com.bigo.common.utils;

import com.bigo.project.bigo.luck.domain.Jackpot;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: wenxm
 * @date: 2020/12/31 19:46
 */
public class TestLottery {

    static final int TIME = 100000;


    public static void main(String[] args) {
       /* List<Jackpot> list = new ArrayList<>();
        list.add(new Jackpot(1L, "ABC", new BigDecimal(10), new BigDecimal(50)));
        list.add(new Jackpot(2L, "BBC", new BigDecimal(11), BigDecimal.ZERO));
        list.add(new Jackpot(3L, "CBC", new BigDecimal(12), BigDecimal.ZERO));
        list.add(new Jackpot(4L, "DBC", new BigDecimal(13), BigDecimal.ZERO));
        list.add(new Jackpot(5L, "EBC", new BigDecimal(14), BigDecimal.ZERO));
        list.add(new Jackpot(6L, "FBC", new BigDecimal(15), BigDecimal.ZERO));
        list.add(new Jackpot(7L, "GBC", new BigDecimal(16), BigDecimal.ZERO));
        list.add(new Jackpot(8L, "HBC", new BigDecimal(17), new BigDecimal(50)));
        for (int i =0; i<100; i++) {
            LotteryUtil ll = new LotteryUtil(list);
            int index = ll.randomColunmIndex();
            System.out.println(list.get(index).getId());
        }*/
        Thread2[] ThreadArr = new Thread2[10];
        for (int i = 0; i < ThreadArr.length; i++) {
            ThreadArr[i] = new Thread2();
            ThreadArr[i].start();
        }
    }

    // 测试线程
    static class Thread2 extends Thread {
        @Override
        public void run() {
            System.out.println(JavaMailLazy.getInstance().hashCode());
        }
    }
        //构造概率集合
//        List<Double> list = new ArrayList<Double>();
//        list.add(50d);
//        list.add(0d);
//        list.add(0d);
//        list.add(0d);
//        list.add(50d);
//        LotteryUtil ll = new LotteryUtil(list);
//        double sumProbability = ll.getMaxElement();
//
//        Map<Integer, Integer> map = new HashMap<Integer, Integer>();
//        for(int i = 0; i < TIME; i++){
//            int index = ll.randomColunmIndex();
//            if(map.containsKey(index)){
//                map.put(index, map.get(index) + 1);
//            }else{
//                map.put(index, 1);
//            }
//        }
//        for(int i = 0; i < list.size(); i++){
//            double probability = list.get(i) / sumProbability;
//            list.set(i, probability);
//        }
//        iteratorMap(map, list);
//
//    }

}

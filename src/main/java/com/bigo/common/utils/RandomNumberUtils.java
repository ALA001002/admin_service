package com.bigo.common.utils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Description
 * @Author wenxm
 * @Date 2020/6/23 14:18
 */
public class RandomNumberUtils {


    private RandomNumberUtils() {}


    // 全局自增数
    private static int count = 1;
    // 格式化的时间字符串
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    // 获取当前时间年月日时分秒毫秒字符串
    public static String getNowDateStr() {
        return sdf.format(new Date());
    }
    // 记录上一次的时间，用来判断是否需要递增全局数
    private static String now = null;
    //定义锁对象
    private final static ReentrantLock lock = new ReentrantLock();
    //调用的方法
    public static String getRandom(String suffix){
        String Newnumber=null;
        String dateStr = getNowDateStr();
        lock.lock();//加锁
        //判断是时间是否相同
        if (dateStr.equals(now)) {
            try {
                if (count >= 10000)
                {
                    count = 1;
                }
                if (count<10) {
                    Newnumber = suffix + getNowDateStr()+"000"+count;
                }else if (count<100) {
                    Newnumber = suffix + getNowDateStr()+"00"+count;
                }else if(count<1000){
                    Newnumber = suffix + getNowDateStr()+"0"+count;
                }else  {
                    Newnumber = suffix + getNowDateStr()+count;
                }
                count++;
            } catch (Exception e) {
            }finally{
                lock.unlock();
            }
        }else{
            count=1;
            now =getNowDateStr();
            try {
                if (count >= 10000)
                {
                    count = 1;
                }
                if (count<10) {
                    Newnumber = suffix + getNowDateStr()+"000"+count;
                }else if (count<100) {
                    Newnumber = suffix + getNowDateStr()+"00"+count;
                }else if(count<1000){
                    Newnumber = suffix + getNowDateStr()+"0"+count;
                }else  {
                    Newnumber = suffix + getNowDateStr()+count;
                }
                count++;
            } catch (Exception e) {
            }finally{
                lock.unlock();
            }
        }
        return Newnumber;
    }

    public static String getRandom(){
        return RandomNumberUtils.getRandom("N");
    }

    public static Long geneRandomUid(){
        Integer random = new Random().nextInt(999999);
        if (random < 100000) {
            random += 100000;
        }
        return random.longValue();
    }

    //随机生成random随机数
    public static BigDecimal randomBigdecimal(Long max, Long min, int scale) {
        BigDecimal cha = new BigDecimal(Math.random() * (max-min) + min);
        return cha.setScale(scale,BigDecimal.ROUND_HALF_UP);//保留 scale 位小数，并四舍五入
    }
    //随机生成random随机数
    public static BigDecimal randomBigdecimal(Double max, Double min, int scale) {
        BigDecimal cha = new BigDecimal(Math.random() * (max-min) + min);
        return cha.setScale(scale,BigDecimal.ROUND_HALF_UP);//保留 scale 位小数，并四舍五入
    }

    /**
     * 生成4位随机数验证码
     * @return
     */
    public static String genCaptcha(){
        String vcode = "";
        for (int i = 0; i < 4; i++) {
            vcode = vcode + (int)(Math.random() * 9);
        }
        return vcode;
    }

    public static void main(String[] args) {
        for(int i=0; i< 100; i++) {
            System.out.println(RandomNumberUtils.getRandom("M_sell")+""+RandomNumberUtils.genCaptcha());
        }
    }
}

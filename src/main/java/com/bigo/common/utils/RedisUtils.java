package com.bigo.common.utils;

import com.bigo.framework.redis.RedisCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Description redis缓存工具类
 * @Author wenxm
 * @Date 2020/6/20 10:02
 */

@Component
@Slf4j
public class RedisUtils {

    @Autowired
    private RedisCache redisCache;

    private static RedisUtils util;

    private RedisUtils(){}

    @PostConstruct
    public void init() {
        util = this;
        util.redisCache = this.redisCache;
    }

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key 缓存的键值
     * @param value 缓存的值
     * @return 缓存的对象
     */
    public static <T> ValueOperations<String, T> setCacheObject(String key, T value){
        return util.redisCache.setCacheObject(key, value);
    }

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key 缓存的键值
     * @param value 缓存的值
     * @param timeout 时间
     * @param timeUnit 时间颗粒度
     * @return 缓存的对象
     */
    public static <T> ValueOperations<String, T> setCacheObject(String key, T value, Integer timeout, TimeUnit timeUnit){
        return util.redisCache.setCacheObject(key, value, timeout, timeUnit);
    }

    /**
     * 获得缓存的基本对象。
     *
     * @param key 缓存键值
     * @return 缓存键值对应的数据
     */
    public static <T> T getCacheObject(String key){
        return util.redisCache.getCacheObject(key);
    }

    /**
     * 删除单个对象
     *
     * @param key
     */
    public static void deleteObject(String key){
        util.redisCache.deleteObject(key);
    }

    /**
     * 删除集合对象
     *
     * @param collection
     */
    public static void deleteObject(Collection collection){
        util.redisCache.deleteObject(collection);
    }

    /**
     * 缓存List数据
     *
     * @param key 缓存的键值
     * @param dataList 待缓存的List数据
     * @return 缓存的对象
     */
    public static <T> ListOperations<String, T> setCacheList(String key, List<T> dataList)
    {
        return util.redisCache.setCacheList(key, dataList);
    }

    /**
     * 获得缓存的list对象
     *
     * @param key 缓存的键值
     * @return 缓存键值对应的数据
     */
    public static <T> List<T> getCacheList(String key)
    {
        return util.redisCache.getCacheList(key);
    }

    /**
     * 缓存Set
     *
     * @param key 缓存键值
     * @param dataSet 缓存的数据
     * @return 缓存数据的对象
     */
    public static <T> BoundSetOperations<String, T> setCacheSet(String key, Set<T> dataSet)
    {
        return util.redisCache.setCacheSet(key, dataSet);
    }

    /**
     * 获得缓存的set
     *
     * @param key
     * @return
     */
    public static <T> Set<T> getCacheSet(String key)
    {
        return util.redisCache.getCacheSet(key);
    }

    /**
     * 缓存Map
     *
     * @param key
     * @param dataMap
     * @return
     */
    public static <T> HashOperations<String, String, T> setCacheMap(String key, Map<String, T> dataMap)
    {
        return util.redisCache.setCacheMap(key, dataMap);
    }

    /**
     * 获得缓存的Map
     *
     * @param key
     * @return
     */
    public static <T> Map<String, T> getCacheMap(String key)
    {
        return util.redisCache.getCacheMap(key);
    }

    /**
     * 获得缓存的基本对象列表
     *
     * @param pattern 字符串前缀
     * @return 对象列表
     */
    public static Collection<String> keys(String pattern)
    {
        return util.redisCache.keys(pattern);
    }
}

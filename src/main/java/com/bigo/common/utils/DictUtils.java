package com.bigo.common.utils;

import java.util.Collection;
import java.util.List;
import com.bigo.common.constant.Constants;
import com.bigo.common.utils.spring.SpringUtils;
import com.bigo.framework.redis.RedisCache;
import com.bigo.project.system.domain.SysDictData;
import com.bigo.project.system.service.ISysDictTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;

/**
 * 字典工具类
 * 
 * @author bigo
 */
@Component
public class DictUtils
{

    @Autowired
    private ISysDictTypeService dictTypeService;

    private static DictUtils util;

    private DictUtils(){}

    @PostConstruct
    public void init() {
        util = this;
        util.dictTypeService = this.dictTypeService;
    }


    /**
     * 设置字典缓存
     * 
     * @param key 参数键
     * @param dictDatas 字典数据列表
     */
    public static void setDictCache(String key, List<SysDictData> dictDatas)
    {
        SpringUtils.getBean(RedisCache.class).setCacheObject(getCacheKey(key), dictDatas);
    }

    /**
     * 获取字典缓存
     * 
     * @param key 参数键
     * @return dictDatas 字典数据列表
     */
    public static List<SysDictData> getDictCache(String key)
    {
        Object cacheObj = SpringUtils.getBean(RedisCache.class).getCacheObject(getCacheKey(key));
        if (StringUtils.isNotNull(cacheObj))
        {
            List<SysDictData> dictDatas = StringUtils.cast(cacheObj);
            return dictDatas;
        }
        return null;
    }

    /**
     * 清空字典缓存
     */
    public static void clearDictCache()
    {
        Collection<String> keys = SpringUtils.getBean(RedisCache.class).keys(Constants.SYS_DICT_KEY + "*");
        SpringUtils.getBean(RedisCache.class).deleteObject(keys);
    }

    /**
     * 设置cache key
     * 
     * @param configKey 参数键
     * @return 缓存键key
     */
    public static String getCacheKey(String configKey)
    {
        return Constants.SYS_DICT_KEY + configKey;
    }

    /**
     * 获取字典值
     *
     * @param type 参数键
     * @return 值
     */
    public static String getDictValue(String type, String label)
    {
        return DictUtils.getDictValue(type, label, null);
    }

    /**
     * 获取字典值（为空返回默认值）
     * @param type 字典类型
     * @param label 字典名
     * @param value 返回的默认值
     * @return
     */
    public static String getDictValue(String type, String label, String value)
    {
        List<SysDictData> dictList = util.dictTypeService.selectDictDataByType(type);
        if(!CollectionUtils.isEmpty(dictList)){
            SysDictData dictData = dictList.stream().filter(a->a.getDictLabel().equals(label)).findFirst().orElse(null);
            if(dictData != null){
                return dictData.getDictValue();
            }
        }
        return value;
    }


}

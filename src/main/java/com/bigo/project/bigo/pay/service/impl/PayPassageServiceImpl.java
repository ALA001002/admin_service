package com.bigo.project.bigo.pay.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.bigo.common.utils.DateUtils;
import com.bigo.project.bigo.pay.domain.PayInterfaceType;
import com.bigo.project.bigo.pay.domain.PayPassage;
import com.bigo.project.bigo.pay.mapper.PayPassageMapper;
import com.bigo.project.bigo.pay.service.IPayInterfaceTypeService;
import com.bigo.project.bigo.pay.service.IPayPassageService;
import com.bigo.project.bigo.pay.vo.ParamVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * 支付通道Service业务层处理
 * 
 * @author bigo
 * @date 2021-05-20
 */
@Service
public class PayPassageServiceImpl implements IPayPassageService
{
    @Autowired
    private PayPassageMapper payPassageMapper;

    @Autowired
    private IPayInterfaceTypeService interfaceTypeService;

    /**
     * 查询支付通道
     * 
     * @param id 支付通道ID
     * @return 支付通道
     */
    @Override
    public PayPassage selectPayPassageById(Long id)
    {
        return payPassageMapper.selectPayPassageById(id);
    }

    /**
     * 查询支付通道列表
     * 
     * @param payPassage 支付通道
     * @return 支付通道
     */
    @Override
    public List<PayPassage> selectPayPassageList(PayPassage payPassage)
    {
        return payPassageMapper.selectPayPassageList(payPassage);
    }

    /**
     * 新增支付通道
     * 
     * @param payPassage 支付通道
     * @return 结果
     */
    @Override
    public int insertPayPassage(PayPassage payPassage) {
        // 查询接口代码
        PayInterfaceType type = interfaceTypeService.selectPayInterfaceTypeById(payPassage.getIfTypeCode());
        if(type != null){
            payPassage.setParam(type.getParam());
        }
        payPassage.setCreateTime(DateUtils.getNowDate());
        return payPassageMapper.insertPayPassage(payPassage);
    }

    /**
     * 修改支付通道
     * 
     * @param payPassage 支付通道
     * @return 结果
     */
    @Override
    public int updatePayPassage(PayPassage payPassage)
    {
        PayPassage oldPassage = payPassageMapper.selectPayPassageById(payPassage.getId());
        if(!oldPassage.getIfTypeCode().equals(payPassage.getIfTypeCode())) {
            PayInterfaceType type = interfaceTypeService.selectPayInterfaceTypeById(payPassage.getIfTypeCode());
            if(type != null) payPassage.setParam(type.getParam());
        }
        payPassage.setUpdateTime(DateUtils.getNowDate());
        return payPassageMapper.updatePayPassage(payPassage);
    }

    /**
     * 批量删除支付通道
     * 
     * @param ids 需要删除的支付通道ID
     * @return 结果
     */
    @Override
    public int deletePayPassageByIds(Long[] ids)
    {
        return payPassageMapper.deletePayPassageByIds(ids);
    }

    /**
     * 删除支付通道信息
     * 
     * @param id 支付通道ID
     * @return 结果
     */
    @Override
    public int deletePayPassageById(Long id)
    {
        return payPassageMapper.deletePayPassageById(id);
    }

    @Override
    public void updateParam(JSONObject object) {
        Long id = object.getLong("id");
        PayPassage payPassage = payPassageMapper.selectPayPassageById(id);
        List<ParamVo> voList = JSONObject.parseArray(payPassage.getParam(), ParamVo.class);
        Set<String> keys = object.keySet();
        for (ParamVo paramVo : voList) {
            for (String key : keys) {
                if(paramVo.getField().equals(key)) {
                    paramVo.setValue(object.getString(key));
                    break;
                }
            }
        }
        String paramJsonStr = JSONObject.toJSONString(voList);
        PayPassage newPaypassage = new PayPassage();
        newPaypassage.setId(id);
        newPaypassage.setParam(paramJsonStr);
        payPassageMapper.updatePayPassage(newPaypassage);
    }
}

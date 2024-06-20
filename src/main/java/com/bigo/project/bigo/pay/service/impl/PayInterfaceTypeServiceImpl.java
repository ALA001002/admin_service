package com.bigo.project.bigo.pay.service.impl;

import com.bigo.common.utils.DateUtils;
import com.bigo.common.utils.StringUtils;
import com.bigo.project.bigo.pay.domain.PayInterfaceType;
import com.bigo.project.bigo.pay.domain.PayPassage;
import com.bigo.project.bigo.pay.mapper.PayInterfaceTypeMapper;
import com.bigo.project.bigo.pay.service.IPayInterfaceTypeService;
import com.bigo.project.bigo.pay.service.IPayPassageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 支付接口类型Service业务层处理
 * 
 * @author bigo
 * @date 2021-05-20
 */
@Service
public class PayInterfaceTypeServiceImpl implements IPayInterfaceTypeService
{
    @Autowired
    private PayInterfaceTypeMapper payInterfaceTypeMapper;

    @Autowired
    private IPayPassageService payPassageService;
    /**
     * 查询支付接口类型
     * 
     * @param ifTypeCode 支付接口类型ID
     * @return 支付接口类型
     */
    @Override
    public PayInterfaceType selectPayInterfaceTypeById(String ifTypeCode)
    {
        return payInterfaceTypeMapper.selectPayInterfaceTypeById(ifTypeCode);
    }

    /**
     * 查询支付接口类型列表
     * 
     * @param payInterfaceType 支付接口类型
     * @return 支付接口类型
     */
    @Override
    public List<PayInterfaceType> selectPayInterfaceTypeList(PayInterfaceType payInterfaceType)
    {
        return payInterfaceTypeMapper.selectPayInterfaceTypeList(payInterfaceType);
    }

    /**
     * 新增支付接口类型
     * 
     * @param payInterfaceType 支付接口类型
     * @return 结果
     */
    @Override
    public int insertPayInterfaceType(PayInterfaceType payInterfaceType)
    {
        payInterfaceType.setCreateTime(DateUtils.getNowDate());
        return payInterfaceTypeMapper.insertPayInterfaceType(payInterfaceType);
    }

    /**
     * 修改支付接口类型
     * 
     * @param payInterfaceType 支付接口类型
     * @return 结果
     */
    @Override
    public int updatePayInterfaceType(PayInterfaceType payInterfaceType)
    {
        payInterfaceType.setUpdateTime(DateUtils.getNowDate());
        return payInterfaceTypeMapper.updatePayInterfaceType(payInterfaceType);
    }

    /**
     * 批量删除支付接口类型
     * 
     * @param ifTypeCodes 需要删除的支付接口类型ID
     * @return 结果
     */
    @Override
    public int deletePayInterfaceTypeByIds(String[] ifTypeCodes)
    {
        return payInterfaceTypeMapper.deletePayInterfaceTypeByIds(ifTypeCodes);
    }

    /**
     * 删除支付接口类型信息
     * 
     * @param ifTypeCode 支付接口类型ID
     * @return 结果
     */
    @Override
    public int deletePayInterfaceTypeById(String ifTypeCode)
    {
        return payInterfaceTypeMapper.deletePayInterfaceTypeById(ifTypeCode);
    }

    @Override
    @Transactional
    public int updateParam(PayInterfaceType payInterfaceType) {
        PayInterfaceType type = payInterfaceTypeMapper.selectPayInterfaceTypeById(payInterfaceType.getIfTypeCode());
        if(StringUtils.isNotEmpty(type.getParam()) && type.getParam().equals(payInterfaceType.getParam())) return 1;

        // 查询有哪几个通道是使用此代码配置
        PayPassage payPassage = new PayPassage();
        payPassage.setIfTypeCode(payInterfaceType.getIfTypeCode());
        List<PayPassage> payPassageList = payPassageService.selectPayPassageList(payPassage);
        if(payPassageList != null && payPassageList.size() > 0) {
            for (PayPassage passage : payPassageList) {
                passage.setIfTypeCode(payInterfaceType.getIfTypeCode());
                passage.setParam(payInterfaceType.getParam());
                payPassageService.updatePayPassage(passage);
            }
        }
        PayInterfaceType newFaceType = new PayInterfaceType();
        newFaceType.setIfTypeCode(payInterfaceType.getIfTypeCode());
        newFaceType.setParam(payInterfaceType.getParam());
        newFaceType.setUpdateTime(new Date());
        return payInterfaceTypeMapper.updatePayInterfaceType(newFaceType);
    }
}

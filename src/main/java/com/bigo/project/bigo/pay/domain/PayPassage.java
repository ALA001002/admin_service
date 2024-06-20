package com.bigo.project.bigo.pay.domain;

import com.alibaba.fastjson.JSONObject;
import com.bigo.common.utils.StringUtils;
import com.bigo.framework.aspectj.lang.annotation.Excel;
import com.bigo.framework.web.domain.BaseEntity;
import com.bigo.project.bigo.pay.vo.ParamVo;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 支付通道对象 bg_pay_passage
 * 
 * @author bigo
 * @date 2021-05-20
 */
@Data
public class PayPassage extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    private Long id;

    /** 通道名称 */
    @Excel(name = "通道名称")
    private String passageName;

    /** 接口类型代码 */
    @Excel(name = "接口类型代码")
    private String ifTypeCode;

    /** 通道状态,0-关闭,1-开启 */
    @Excel(name = "通道状态,0-关闭,1-开启")
    private Integer status;

    /** 单笔最大金额 */
    @Excel(name = "单笔最大金额")
    private Long maxEveryAmount;

    /** 单笔最小金额 */
    @Excel(name = "单笔最小金额")
    private Long minEveryAmount;

    /** 账户配置参数,json字符串 */
    @Excel(name = "账户配置参数,json字符串")
    private String param;

    public List<ParamVo> getParamList() {
        if(StringUtils.isNotEmpty(param)) {
            List<ParamVo> paramVoList = JSONObject.parseArray(param, ParamVo.class);
            return paramVoList;
        }
        return new ArrayList<ParamVo>();
    }
}

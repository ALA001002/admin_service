package com.bigo.project.bigo.product.domain;

import com.bigo.framework.aspectj.lang.annotation.Excel;
import com.bigo.framework.web.domain.BaseEntity;
import lombok.Data;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * 理财产品类型对象 bg_product_type
 * 
 * @author bigo
 * @date 2022-03-22
 */
@Data
public class ProductType extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** id */
    private Long id;

    /** 类型名称 */
    @Excel(name = "类型名称")
    private String typeName;

    /** 状态 */
    @Excel(name = "状态")
    private Long status;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("typeName", getTypeName())
            .append("status", getStatus())
            .append("createTime", getCreateTime())
            .toString();
    }
}

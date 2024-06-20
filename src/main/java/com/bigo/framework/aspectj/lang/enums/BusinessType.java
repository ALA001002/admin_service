package com.bigo.framework.aspectj.lang.enums;

/**
 * 业务操作类型
 * 
 * @author bigo
 */
public enum BusinessType
{
    /**
     * 其它
     */
    OTHER,

    /**
     * 新增
     */
    INSERT,

    /**
     * 修改
     */
    UPDATE,

    /**
     * 删除
     */
    DELETE,

    /**
     * 授权
     */
    GRANT,

    /**
     * 导出
     */
    EXPORT,

    /**
     * 导入
     */
    IMPORT,

    /**
     * 强退
     */
    FORCE,

    /**
     * 生成代码
     */
    GENCODE,
    
    /**
     * 清空数据
     */
    CLEAN,

    /**
     * 变更状态
     */
    CHANGE_STATUS,
    /**
     * 审核
     */
    CHECK,
    /**
     * 审核
     */
    LOGIN,
    /**
     * 身份审核
     */
    AUTH_CHECK,
    /**
     * 一键强平
     */
    ONE_KEY_CLOSE,
    /**
     * 一键强平
     */
    INSIDE_RECHARGE,
    /**
     * 开始滑点
     */
    START_SLIP_DOT,
    /**
     * 停止滑点
     */
    STOP_SLIP_DOT,
    /**
     * 设置邀请人
     */
    SET_PARENT,
    /**
     * 提现
     */
    WITHDRAW,
}

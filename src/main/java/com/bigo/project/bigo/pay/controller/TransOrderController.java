package com.bigo.project.bigo.pay.controller;

import java.util.List;

import com.bigo.common.utils.ServletUtils;
import com.bigo.common.utils.google.GoogleAuthenticator;
import com.bigo.framework.security.LoginUser;
import com.bigo.framework.security.service.TokenService;
import com.bigo.project.bigo.pay.domain.PayInterfaceType;
import com.bigo.project.bigo.pay.domain.PayPassage;
import com.bigo.project.bigo.pay.service.IPayInterfaceTypeService;
import com.bigo.project.bigo.pay.service.IPayPassageService;
import com.bigo.project.system.domain.SysUser;
import com.bigo.project.system.service.ISysUserService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.loadtime.Aj;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.bigo.framework.aspectj.lang.annotation.Log;
import com.bigo.framework.aspectj.lang.enums.BusinessType;
import com.bigo.project.bigo.pay.domain.TransOrder;
import com.bigo.project.bigo.pay.service.ITransOrderService;
import com.bigo.framework.web.controller.BaseController;
import com.bigo.framework.web.domain.AjaxResult;
import com.bigo.common.utils.poi.ExcelUtil;
import com.bigo.framework.web.page.TableDataInfo;

/**
 * 代付Controller
 * 
 * @author bigo
 * @date 2022-05-22
 */
@Slf4j
@RestController
@RequestMapping("/pay/trans")
public class TransOrderController extends BaseController
{
    @Autowired
    private ITransOrderService transOrderService;

    @Autowired
    private TokenService tokenService;


    @Autowired
    private ISysUserService sysUserService;

    @Autowired
    private IPayPassageService payPassageService;

    @Autowired
    private IPayInterfaceTypeService payInterfaceTypeService;

    /**
     * 查询代付列表
     */
    @PreAuthorize("@ss.hasPermi('pay:trans:list')")
    @GetMapping("/list")
    public TableDataInfo list(TransOrder transOrder)
    {
        startPage();
        List<TransOrder> list = transOrderService.selectTransOrderList(transOrder);
        return getDataTable(list);
    }

    /**
     * 导出代付列表
     */
    @PreAuthorize("@ss.hasPermi('pay:trans:export')")
    @Log(title = "代付", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(TransOrder transOrder)
    {
        List<TransOrder> list = transOrderService.selectTransOrderList(transOrder);
        ExcelUtil<TransOrder> util = new ExcelUtil<TransOrder>(TransOrder.class);
        return util.exportExcel(list, "trans");
    }

    /**
     * 获取代付详细信息
     */
    @PreAuthorize("@ss.hasPermi('pay:trans:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(transOrderService.selectTransOrderById(id));
    }

    /**
     * 新增代付
     */
    @PreAuthorize("@ss.hasPermi('pay:trans:add')")
    @Log(title = "代付", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody TransOrder transOrder)
    {
        return toAjax(transOrderService.insertTransOrder(transOrder));
    }

    /**
     * 修改代付
     */
    @PreAuthorize("@ss.hasPermi('pay:trans:edit')")
    @Log(title = "代付", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody TransOrder transOrder)
    {
        return toAjax(transOrderService.updateTransOrder(transOrder));
    }

    /**
     * 删除代付
     */
    @PreAuthorize("@ss.hasPermi('pay:trans:remove')")
    @Log(title = "代付", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(transOrderService.deleteTransOrderByIds(ids));
    }

    @PreAuthorize("@ss.hasPermi('pay:trans:edit')")
    @Log(title = "批量审核代付", businessType = BusinessType.UPDATE)
    @PutMapping("/batchCheckTrans")
    public AjaxResult batchCheckTrans(@RequestBody TransOrder entity) {

        if(entity.getIds() == null || entity.getIds().length <= 0){
            return AjaxResult.error("申请ID不能为空");
        }

        if(entity.getCheckStatus() == 1) {
            LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
            SysUser sysUser = loginUser.getUser();
            if (sysUser.getUserId() != 1) return AjaxResult.error("请使用超管账号操作");
            sysUser = sysUserService.selectUserById(sysUser.getUserId());
            if (entity.getGoogleCaptcha() == null || !GoogleAuthenticator.checkGoogleCode(sysUser.getGoogleAuthSecretKey(), entity.getGoogleCaptcha())) {
                return AjaxResult.error("谷歌验证码不正确");
            }
        }
        PayPassage payPassage = null;
        PayInterfaceType interfaceType = null;
        if(entity.getCheckStatus() == 1) {
            payPassage = payPassageService.selectPayPassageById(entity.getPayPassageId());
            if (payPassage == null) AjaxResult.error("请选择代付通道");

            interfaceType = payInterfaceTypeService.selectPayInterfaceTypeById(payPassage.getIfTypeCode());
            if (interfaceType == null) return AjaxResult.error("未找到支付类型");  // 支付中心下单失败
        }

        for (Long id : entity.getIds()) {
            TransOrder order = transOrderService.selectTransOrderById(id);
            if(order == null){
                log.info("未找到代付记录，id={}", id);
                continue;
            }
            if(order.getStatus() != 0) {
                return AjaxResult.error("代付申请已审核");
            }
            order.setPayPassageId(entity.getPayPassageId());
            if(entity.getCheckStatus() == 1) {
                transOrderService.agetnPay(order, payPassage, interfaceType);
            }else {
                transOrderService.rejected(order);
            }
        }
        return AjaxResult.success();
    }

}

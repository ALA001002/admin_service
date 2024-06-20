package com.bigo.project.bigo.product.controller;

import com.alibaba.fastjson.JSONObject;
import com.bigo.common.utils.ServletUtils;
import com.bigo.common.utils.google.GoogleAuthenticator;
import com.bigo.common.utils.poi.ExcelUtil;
import com.bigo.framework.aspectj.lang.annotation.Log;
import com.bigo.framework.aspectj.lang.enums.BusinessType;
import com.bigo.framework.security.LoginUser;
import com.bigo.framework.security.service.TokenService;
import com.bigo.framework.web.controller.BaseController;
import com.bigo.framework.web.domain.AjaxResult;
import com.bigo.framework.web.page.TableDataInfo;
import com.bigo.project.bigo.agent.domain.Agent;
import com.bigo.project.bigo.agent.service.IAgentService;
import com.bigo.project.bigo.product.domain.ProductOrder;
import com.bigo.project.bigo.product.service.IProductOrderService;
import com.bigo.project.system.domain.SysUser;
import com.bigo.project.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 理财产品订单Controller
 * 
 * @author bigo
 * @date 2021-01-27
 */
@RestController
@RequestMapping("/product/productOrder")
public class ProductOrderController extends BaseController
{
    @Autowired
    private IProductOrderService productOrderService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ISysUserService sysUserService;

    @Autowired
    private IAgentService agentService;

    /**
     * 查询理财产品订单列表
     */
    @PreAuthorize("@ss.hasPermi('product:productOrder:list')")
    @GetMapping("/list")
    public TableDataInfo list(ProductOrder productOrder, HttpServletRequest request)
    {
        LoginUser loginUser = tokenService.getLoginUser(request);
        SysUser sysUser = loginUser.getUser();
        Agent agent = agentService.getByUserId(sysUser.getUserId());
        //代理商只能看到自己的信息
        if(agent != null){
            productOrder.setAgentId(agent.getAgentCode());
        }
        startPage();
        List<ProductOrder> list = productOrderService.selectProductOrderList(productOrder);
        return getDataTable(list);
    }

    /**
     * 强制中断释放
     */
    @PreAuthorize("@ss.hasPermi('product:productOrder:end')")
    @Log(title = "强制中断释放", businessType = BusinessType.UPDATE)
    @GetMapping("/stopRelease/{id}")
    public AjaxResult stopRelease(@PathVariable Long id) {
        ProductOrder order = productOrderService.selectProductOrderById(id);
        if (order == null) return AjaxResult.error("该订单不存在");
        if(order.getStatus() == 2) return AjaxResult.error("该订单已释放");
        try {
            Boolean locked = redisTemplate.opsForValue().setIfAbsent(order.getId().toString(), "", 10, TimeUnit.SECONDS);
            if(!locked) return AjaxResult.error("强制中断失败，该订单正在每日释放中！");
            productOrderService.stopRelease(order);
        } finally {
            redisTemplate.delete(order.getId().toString());
        }
        return AjaxResult.success();
    }

    /**
     * 批量强制中断释放
     */
    @PreAuthorize("@ss.hasPermi('product:productOrder:end')")
    @Log(title = "批量强制中断释放", businessType = BusinessType.UPDATE)
    @PostMapping("/batchStopRelease")
    public AjaxResult batchStopRelease(@RequestBody ProductOrder entity) {

        if(entity.getIds() == null || entity.getIds().length <= 0){
            return AjaxResult.error("批量中断ID不能为空");
        }

        LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
        SysUser sysUser = loginUser.getUser();
        if (sysUser.getUserId() != 1) return AjaxResult.error("请使用超管账号操作");
        sysUser = sysUserService.selectUserById(sysUser.getUserId());
        if (entity.getGoogleCaptcha() == null || !GoogleAuthenticator.checkGoogleCode(sysUser.getGoogleAuthSecretKey(), entity.getGoogleCaptcha())) {
            return AjaxResult.error("谷歌验证码不正确");
        }
        for (Long id : entity.getIds()) {
            ProductOrder order = productOrderService.selectProductOrderById(id);
            if (order == null) continue;
            if(order.getStatus() == 2) continue;
            try {
                Boolean locked = redisTemplate.opsForValue().setIfAbsent(order.getId().toString(), "", 10, TimeUnit.SECONDS);
                if(!locked) continue;
                productOrderService.stopRelease(order);
            } finally {
                redisTemplate.delete(order.getId().toString());
            }
        }

        return AjaxResult.success();
    }

    /**
     * 导出理财产品订单列表
     */
    @PreAuthorize("@ss.hasPermi('product:productOrder:export')")
    @Log(title = "理财产品订单", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(ProductOrder productOrder)
    {
        List<ProductOrder> list = productOrderService.selectProductOrderList(productOrder);
        ExcelUtil<ProductOrder> util = new ExcelUtil<ProductOrder>(ProductOrder.class);
        return util.exportExcel(list, "productOrder");
    }

    /**
     * 获取理财产品订单详细信息
     */
    @PreAuthorize("@ss.hasPermi('product:productOrder:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(productOrderService.selectProductOrderById(id));
    }

    /**
     * 新增理财产品订单
     */
    @PreAuthorize("@ss.hasPermi('product:productOrder:add')")
    @Log(title = "理财产品订单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ProductOrder productOrder)
    {
        return toAjax(productOrderService.insertProductOrder(productOrder));
    }

    /**
     * 修改理财产品订单
     */
    @PreAuthorize("@ss.hasPermi('product:productOrder:edit')")
    @Log(title = "理财产品订单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ProductOrder productOrder)
    {
        return toAjax(productOrderService.updateProductOrder(productOrder));
    }

    /**
     * 删除理财产品订单
     */
    @PreAuthorize("@ss.hasPermi('product:productOrder:remove')")
    @Log(title = "理财产品订单", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(productOrderService.deleteProductOrderByIds(ids));
    }
}

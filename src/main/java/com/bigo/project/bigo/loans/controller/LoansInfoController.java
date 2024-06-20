package com.bigo.project.bigo.loans.controller;

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
import com.bigo.project.bigo.loans.domain.LoansInfo;
import com.bigo.project.bigo.loans.dto.LoansCheckDTO;
import com.bigo.project.bigo.loans.service.ILoansInfoService;
import com.bigo.project.system.domain.SysUser;
import com.bigo.project.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 借款信息Controller
 * 
 * @author bigo
 * @date 2022-01-12
 */
@RestController
@RequestMapping("/loans/LoansInfo")
public class LoansInfoController extends BaseController
{
    @Autowired
    private ILoansInfoService loansInfoService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private ISysUserService sysUserService;

    /**
     * 查询借款信息列表
     */
    @PreAuthorize("@ss.hasPermi('loans:LoansInfo:list')")
    @GetMapping("/list")
    public TableDataInfo list(LoansInfo loansInfo)
    {
        startPage();
        List<LoansInfo> list = loansInfoService.selectLoansInfoByWithdrawList(loansInfo);
        return getDataTable(list);
    }

    /**
     * 导出借款信息列表
     */
    @PreAuthorize("@ss.hasPermi('loans:LoansInfo:export')")
    @Log(title = "借款信息", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(LoansInfo loansInfo)
    {
        List<LoansInfo> list = loansInfoService.selectLoansInfoList(loansInfo);
        ExcelUtil<LoansInfo> util = new ExcelUtil<LoansInfo>(LoansInfo.class);
        return util.exportExcel(list, "LoansInfo");
    }

    /**
     * 获取借款信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('loans:LoansInfo:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(loansInfoService.selectLoansInfoById(id));
    }

    /**
     * 新增借款信息
     */
    @PreAuthorize("@ss.hasPermi('loans:LoansInfo:add')")
    @Log(title = "借款信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LoansInfo loansInfo)
    {
        return toAjax(loansInfoService.insertLoansInfo(loansInfo));
    }

    /**
     * 修改借款信息
     */
    @PreAuthorize("@ss.hasPermi('loans:LoansInfo:edit')")
    @Log(title = "借款信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LoansInfo loansInfo)
    {
        return toAjax(loansInfoService.updateLoansInfo(loansInfo));
    }

    /**
     * 删除借款信息
     */
    @PreAuthorize("@ss.hasPermi('loans:LoansInfo:remove')")
    @Log(title = "借款信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(loansInfoService.deleteLoansInfoByIds(ids));
    }


    @PreAuthorize("@ss.hasPermi('loans:LoansInfo:export')")
    @Log(title = "批量审核", businessType = BusinessType.UPDATE)
    @PostMapping("/batchCheck")
    public AjaxResult batchCheck(@RequestBody LoansCheckDTO dto, HttpServletRequest request) {
        if(dto.getIds() == null || dto.getIds().length <= 0){
            return AjaxResult.error("审核ID不能为空");
        }

        if(dto.getStatus() == 3) {
            LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
            SysUser sysUser = loginUser.getUser();
            if (sysUser.getUserId() != 1) return AjaxResult.error("请使用超管账号操作");
            sysUser = sysUserService.selectUserById(sysUser.getUserId());
            if (dto.getGoogleCaptcha() == null || !GoogleAuthenticator.checkGoogleCode(sysUser.getGoogleAuthSecretKey(), dto.getGoogleCaptcha())) {
                return AjaxResult.error("谷歌验证码不正确");
            }
        }
        for (Long id : dto.getIds()) {
            LoansInfo info = loansInfoService.selectLoansInfoById(id);
            if(info == null) return AjaxResult.error("订单不存在,ID="+id);
            if(info.getStatus() != 1) return AjaxResult.error("订单已审核,ID="+id);
            LoginUser user = tokenService.getLoginUser(request);
            info.setOperatorId(user.getUser().getUserId());
            loansInfoService.checkLoansInfo(info, dto.getStatus());
        }

        return AjaxResult.success();
    }
}

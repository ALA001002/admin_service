package com.bigo.project.bigo.luck.controller;

import java.util.List;

import com.bigo.common.utils.CoinUtils;
import com.bigo.project.bigo.wallet.domain.Currency;
import com.bigo.project.bigo.wallet.service.ICurrencyService;
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
import com.bigo.project.bigo.luck.domain.Jackpot;
import com.bigo.project.bigo.luck.service.IJackpotService;
import com.bigo.framework.web.controller.BaseController;
import com.bigo.framework.web.domain.AjaxResult;
import com.bigo.common.utils.poi.ExcelUtil;
import com.bigo.framework.web.page.TableDataInfo;

/**
 * 奖池Controller
 * 
 * @author bigo
 * @date 2020-12-31
 */
@RestController
@RequestMapping("/luck/jackpot")
public class JackpotController extends BaseController
{
    @Autowired
    private IJackpotService jackpotService;

    @Autowired
    private ICurrencyService currencyService;

    /**
     * 查询奖池列表
     */
    @PreAuthorize("@ss.hasPermi('luck:jackpot:list')")
    @GetMapping("/list")
    public TableDataInfo list(Jackpot jackpot)
    {
        startPage();
        List<Jackpot> list = jackpotService.selectJackpotList(jackpot);
        return getDataTable(list);
    }

    /**
     * 导出奖池列表
     */
    @PreAuthorize("@ss.hasPermi('luck:jackpot:export')")
    @Log(title = "奖池", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(Jackpot jackpot)
    {
        List<Jackpot> list = jackpotService.selectJackpotList(jackpot);
        ExcelUtil<Jackpot> util = new ExcelUtil<Jackpot>(Jackpot.class);
        return util.exportExcel(list, "jackpot");
    }

    /**
     * 获取奖池详细信息
     */
    @PreAuthorize("@ss.hasPermi('luck:jackpot:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(jackpotService.selectJackpotById(id));
    }

    /**
     * 新增奖池
     */
    @PreAuthorize("@ss.hasPermi('luck:jackpot:add')")
    @Log(title = "奖池", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Jackpot jackpot)
    {
        Currency currency = currencyService.getByCode(jackpot.getCoin().toUpperCase());
        //校验币种是否存在
        if(currency == null) {
            return AjaxResult.error("不支持该币种");
        }
        jackpot.setCoin(jackpot.getCoin().toUpperCase());
        return toAjax(jackpotService.insertJackpot(jackpot));
    }

    /**
     * 修改奖池
     */
    @PreAuthorize("@ss.hasPermi('luck:jackpot:edit')")
    @Log(title = "奖池", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Jackpot jackpot)
    {
        Currency currency = currencyService.getByCode(jackpot.getCoin().toUpperCase());
        //校验币种是否存在
        if(currency == null) {
            return AjaxResult.error("不支持该币种");
        }
        jackpot.setCoin(jackpot.getCoin().toUpperCase());
        return toAjax(jackpotService.updateJackpot(jackpot));
    }

    /**
     * 删除奖池
     */
    @PreAuthorize("@ss.hasPermi('luck:jackpot:remove')")
    @Log(title = "奖池", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(jackpotService.deleteJackpotByIds(ids));
    }
}

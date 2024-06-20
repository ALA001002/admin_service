package com.bigo.project.bigo.agent.controller;

import com.bigo.common.constant.UserConstants;
import com.bigo.common.utils.SecurityUtils;
import com.bigo.common.utils.ServletUtils;
import com.bigo.common.utils.StringUtils;
import com.bigo.common.utils.poi.ExcelUtil;
import com.bigo.framework.aspectj.lang.annotation.Log;
import com.bigo.framework.aspectj.lang.enums.BusinessType;
import com.bigo.framework.security.LoginUser;
import com.bigo.framework.security.service.TokenService;
import com.bigo.framework.web.controller.BaseController;
import com.bigo.framework.web.domain.AjaxResult;
import com.bigo.framework.web.page.TableDataInfo;
import com.bigo.project.bigo.agent.domain.Agent;
import com.bigo.project.bigo.agent.domain.AgentAssetLog;
import com.bigo.project.bigo.agent.domain.AgentRegisterParam;
import com.bigo.project.bigo.agent.domain.AgentWithdraw;
import com.bigo.project.bigo.agent.service.IAgentAssetLogService;
import com.bigo.project.bigo.agent.service.IAgentService;
import com.bigo.project.bigo.agent.vo.UserChildrenVo;
import com.bigo.project.bigo.agent.vo.UserSumVo;
import com.bigo.project.bigo.userinfo.entity.BigoUserEntity;
import com.bigo.project.bigo.userinfo.service.IBigoUserService;
import com.bigo.project.bigo.wallet.entity.AssetLogEntity;
import com.bigo.project.bigo.wallet.entity.WalletEntity;
import com.bigo.project.bigo.wallet.service.IAssetLogService;
import com.bigo.project.system.domain.SysRole;
import com.bigo.project.system.domain.SysUser;
import com.bigo.project.system.service.ISysPostService;
import com.bigo.project.system.service.ISysRoleService;
import com.bigo.project.system.service.ISysUserService;
import com.google.common.collect.Maps;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.Result;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户信息
 * 
 * @author bigo
 */
@RestController
@RequestMapping("/agent")
public class BigoAgentController extends BaseController
{
    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysRoleService roleService;

    @Autowired
    private ISysPostService postService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private IAgentService agentService;

    @Autowired
    private IAgentAssetLogService agentAssetLogService;

    @Autowired
    private IBigoUserService bigoUserService;

    @Autowired
    private IAssetLogService assetLogService;

    /**
     * 获取代理商列表
     */
    @PreAuthorize("@ss.hasPermi('bigo:agent:list')")
    @GetMapping("/list")
    public TableDataInfo list(Agent param, HttpServletRequest request)
    {
        LoginUser loginUser = tokenService.getLoginUser(request);
        SysUser user = loginUser.getUser();
        Agent agent = agentService.getByUserId(user.getUserId());
        //代理商只能看到自己的信息
        if(agent != null){
            param.setAgentId(agent.getAgentId());
        }
        startPage();
        List<Agent> list = agentService.listByEntity(param);
        return getDataTable(list);
    }


    /**
     * 下级用户
     * @param entity
     * @param request
     * @return
     */
    @PreAuthorize("@ss.hasPermi('bigo:agent:childrenList')")
    @GetMapping("/childrenList")
    public TableDataInfo childrenList(BigoUserEntity entity, HttpServletRequest request) {
        LoginUser loginUser = tokenService.getLoginUser(request);
        SysUser sysUser = loginUser.getUser();
        //查询当前账号是否为代理商
        Agent agent = agentService.getByUserId(sysUser.getUserId());
        //查询所有的代理商

        StringBuilder sb = new StringBuilder();
        List<Agent> agentList = null;
        if(agent == null) {
            agentList = agentService.listByEntity(new Agent());
            for (Agent ag : agentList) {
                sb.append(ag.getAgentCode()).append(",");
            }
        } else {
            sb.append(agent.getAgentCode()).append(",");
        }
        if(StringUtils.isEmpty(sb)) return getDataTable(null);
        // 查询出所有下级
        entity.setAgentIds(sb.substring(0, sb.length()-1));
        startPage();
        List<BigoUserEntity> list = bigoUserService.getChildrenList(entity);
        return getDataTable(list);
    }

    /**
     * 下级用户资产变更列表
     * @param request
     * @return
     */
    @PreAuthorize("@ss.hasPermi('bigo:agent:childrenList')")
    @GetMapping("/childrenAssetLog")
    public TableDataInfo childrenAssetLog(AssetLogEntity log, HttpServletRequest request) {
        LoginUser loginUser = tokenService.getLoginUser(request);
        SysUser sysUser = loginUser.getUser();
        //查询当前账号是否为代理商
        Agent agent = agentService.getByUserId(sysUser.getUserId());
        //查询所有的代理商

        StringBuilder sb = new StringBuilder();
        List<Agent> agentList = null;
        if(agent == null) {
            agentList = agentService.listByEntity(new Agent());
            for (Agent ag : agentList) {
                sb.append(ag.getAgentCode()).append(",");
            }
        } else {
            sb.append(agent.getAgentCode()).append(",");
        }
        if(StringUtils.isEmpty(sb)) return getDataTable(null);
        // 查询出所有下级
        BigoUserEntity userEntity = new BigoUserEntity();
        userEntity.setAgentIds(sb.substring(0, sb.length()-1));
        List<BigoUserEntity> userList = bigoUserService.getChildrenList(userEntity);
        if (userList == null || userList.size() < 1) return getDataTable(null);
        sb = new StringBuilder();
        for (BigoUserEntity bigoUserEntity : userList) {
            sb.append(bigoUserEntity.getUid()).append(",");
        }
        log.setUids(sb.substring(0, sb.length()-1));
        startPage();
        List<AssetLogEntity> logList = assetLogService.listAssetLogByEntity(log);
        return getDataTable(logList);
    }


    /**
     * 新增代理商
     */
    @PreAuthorize("@ss.hasPermi('bigo:agent:add')")
    @Log(title = "新增代理商", businessType = BusinessType.INSERT)
    @PostMapping("/addAgent")
    public AjaxResult add(@Validated @RequestBody AgentRegisterParam param) throws IOException {
        return toAjax(agentService.addAgent(param));
    }

    /**
     * 新增代理商
     */
    @PreAuthorize("@ss.hasPermi('bigo:agent:edit')")
    @Log(title = "修改代理商信息", businessType = BusinessType.UPDATE)
    @PutMapping("/updateAgent")
    public AjaxResult updateAgent(@RequestBody AgentRegisterParam param)
    {
        if(param.getCashDeposit() == null || param.getFeeShareRate() == null ||
            param.getProfitShareRate() == null || param.getNickName() == null){
            return AjaxResult.error("参数错误");
        }
        Agent agent = new Agent();
        agent.setAgentId(param.getAgentId());
        agent.setAgentName(param.getNickName());
        agent.setFeeShareRate(param.getFeeShareRate());
        agent.setProfitShareRate(param.getProfitShareRate());
        return toAjax(agentService.updateAgent(agent));
    }

    /**
     * 新增代理商
     */
    @PreAuthorize("@ss.hasPermi('bigo:agent:edit')")
    @Log(title = "冻结/解禁代理商", businessType = BusinessType.CHANGE_STATUS)
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody Agent param)
    {
        if(param.getAgentId() == null || param.getStatus() == null || param.getStatus() > 1){
            return AjaxResult.error("参数错误");
        }
        return toAjax(agentService.updateStatus(param));
    }

    /**
     * 代理商资产记录列表
     */
    @PreAuthorize("@ss.hasPermi('bigo:agent:assetlog')")
    @GetMapping("/assetlog/list")
    public Map assetLogList(AgentAssetLog param,HttpServletRequest request)
    {
        LoginUser loginUser = tokenService.getLoginUser(request);
        SysUser user = loginUser.getUser();
        Agent agent = agentService.getByUserId(user.getUserId());
        //代理商只能看到自己的信息
        if(agent != null){
            param.setAgentId(agent.getAgentId());
        }
        startPage();
        List<AgentAssetLog> list = agentAssetLogService.listByEntity(param);
        Map<String, Object> map = Maps.newHashMap();
        TableDataInfo data = getDataTable(list);
        map.put("tableDate",data);
        map.put("agent", agent);
        return map;
    }

    /**
     * 代理商提现
     */
    @PreAuthorize("@ss.hasPermi('bigo:agent:withdraw')")
    @Log(title = "代理商提现", businessType = BusinessType.WITHDRAW)
    @PostMapping("/withdraw")
    public AjaxResult withdraw(@RequestBody AgentWithdraw withdraw, HttpServletRequest request)
    {
        LoginUser loginUser = tokenService.getLoginUser(request);
        SysUser user = loginUser.getUser();
        Agent agent = agentService.getByUserId(user.getUserId());
        if(withdraw.getAmount() == null || withdraw.getCoin() == null || agent == null){
            return AjaxResult.error("参数错误");
        }
        if(agent.getStatus() != 0){
            return AjaxResult.error("账户已被冻结，无法进行提币操作");
        }
        if(withdraw.getToAddress() == null){
            return AjaxResult.error("收币地址不能为空");
        }
        withdraw.setAgentId(agent.getAgentId());
        agentService.withdraw(withdraw);
        return AjaxResult.success();
    }

    /**
     * 统计
     * @return
     */
    @PreAuthorize("@ss.hasPermi('bigo:agent:list')")
    @GetMapping("/statistics")
    public AjaxResult statistics(WalletEntity entity, HttpServletRequest request) {
        LoginUser loginUser = tokenService.getLoginUser(request);
        SysUser user = loginUser.getUser();
        Agent agent = agentService.getByUserId(user.getUserId());
        //代理商只能看到自己的信息
        Long agentId = null;
        if(agent != null){
            agentId = agent.getAgentCode();
            entity.setAgentId(agentId);
        }
        List<UserChildrenVo> childrenVos = agentService.statistics(entity);
//        List<UserChildrenVo> childrenVos = new ArrayList<>();
//        UserChildrenVo vo = null;
        /*for (UserSumVo userSumVo : voList) {
            vo = new UserChildrenVo();
            vo.setUid(userSumVo.getUid());
            vo.setParentId(userSumVo.getParentUid()==null?0:userSumVo.getParentUid());
            vo.setUsername(userSumVo.getUsername());
            vo.setBtc(userSumVo.getBtc().toPlainString());
            vo.setEth(userSumVo.getEth().toPlainString());
            vo.setUsdt(userSumVo.getUsdt().toPlainString());
            vo.setChildren(userSumVo.getChildren());
            childrenVos.add(vo);
        }*/
        if(childrenVos != null && childrenVos.size() >0) childrenVos = childrenVos.stream().distinct().collect(Collectors.toList());
        return AjaxResult.success(childrenVos);
    }

    /**
     * 总表统计
     */
    @PreAuthorize("@ss.hasPermi('bigo:agent:total')")
    @GetMapping("/totalStatistics")
    public AjaxResult totalStatistics(@Param("agentId") Long agentId,
                                      @Param("beginTime") String beginTime,
                                      @Param("endTime") String endTime,
                                      HttpServletRequest request) {
        LoginUser loginUser = tokenService.getLoginUser(request);
        SysUser user = loginUser.getUser();
        Map params = new HashMap();
        Agent agent = agentService.getByUserId(user.getUserId());
        if(agentId != null) {
            params.put("agentId", agentId);
        }
        if(StringUtils.isNotEmpty(beginTime)) {
            params.put("startTime", beginTime+" 00:00:00");
        }
        if(StringUtils.isNotEmpty(endTime)) {
            params.put("endTime", endTime+" 23:59:59");
        }
        //代理商只能看到自己的信息
        if(agent != null){
            agentId = agent.getAgentCode();
            params.put("agentId", agentId);
        }
        Map map = agentService.totalStatistics(params);
        return AjaxResult.success(map);
    }

}
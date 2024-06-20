package com.bigo.project.bigo.contract.mapper;

import com.bigo.project.bigo.api.domain.ContractQueryParam;
import com.bigo.project.bigo.api.vo.ContractVO;
import com.bigo.project.bigo.contract.domain.Contract;
import com.bigo.project.bigo.contract.entity.ContractEntity;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Description 合约mapper
 * @Author wenxm
 * @Date 2020/6/16 10:58
 */
public interface ContractMapper {

    /**
     * 新增合约
     * @param contract
     * @return
     */
    Long insertContract(Contract contract);

    /**
     * 查询合约VO列表
     * @param param
     * @return
     */
    List<ContractVO> listContractVO(ContractQueryParam param);

    /**
     * 查询合约
     * @param param
     * @return
     */
    List<Contract> listContract(Contract param);

    /**
     * 根据用户id和合约id获取合约
     * @param param
     * @return
     */
    Contract getByUidAndContractId(ContractQueryParam param);

    /**
     * 平仓
     * @param contract
     * @return
     */
    int closeContract(Contract contract);

    /**
     * 更新合约信息
     * @param contract
     * @return
     */
    Boolean updateCapitalFee(Contract contract);

    /**
     * 更新合约信息
     * @param contract
     * @return
     */
    Boolean updateStopInfo(Contract contract);

    /**
     * 查询合约
     * @param param
     * @return
     */
    List<Contract> listContractByMap(Map<String, Object> param);

    /**
     * 加补仓费
     * @param contract
     * @return
     */
    int updateReplenish(Contract contract);

    /**
     * 查询合约
     * @param entity
     * @return
     */
    List<ContractEntity> listByEntity(ContractEntity entity);

    /**
     * 根据id获取
     * @param id
     * @return
     */
    Contract getById(Long id);

    /**
     * 获取用户购买合约的总手续费
     * @param uid
     * @param currency
     * @return
     */
    BigDecimal getTotalFeeByUid(@Param("uid") Long uid, @Param("currency") String currency);

    /**
     * 获取用户持仓中合约的总金额
     * @param uid
     * @param currency
     * @return
     */
    BigDecimal getTotalAmountByUid(@Param("uid") Long uid, @Param("currency") String currency);

}

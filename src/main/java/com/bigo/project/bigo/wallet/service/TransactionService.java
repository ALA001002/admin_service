package com.bigo.project.bigo.wallet.service;

import com.bigo.common.utils.StringUtils;
import com.bigo.framework.web.domain.AjaxResult;
import com.bigo.project.bigo.wallet.dao.TransactionRepository;
import com.bigo.project.bigo.wallet.jpaEntity.ViewTronTransactionRecharge;
import com.bigo.project.bigo.wallet.jpaEntity.ViewTronTransactionWithdraw;
import com.bigo.project.bigo.wallet.jpaEntity.Transaction;
import com.bigo.project.bigo.wallet.dao.ViewTronTransactionRechargeRepository;
import com.bigo.project.bigo.wallet.dao.ViewTronTransactionWithdrawRepository;
import com.bigo.project.bigo.wallet.view.TransactionReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class TransactionService {

    @Resource
    ViewTronTransactionRechargeRepository viewTronTransactionRechargeRepository;

    @Resource
    ViewTronTransactionWithdrawRepository viewTronTransactionWithdrawRepository;

    @Resource
    TransactionRepository transactionRepository;

    public Specification<Transaction> createSpecification(int type, String address){
        return ((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicateList = new ArrayList<>();
            predicateList.add(criteriaBuilder.equal(root.get("type"),type));
            if(!StringUtils.isEmpty(address)){
                List<Predicate> or = new ArrayList<>();
                or.add(criteriaBuilder.equal(root.get("toAddress"),address));
                or.add(criteriaBuilder.equal(root.get("fromAddress"),address));
                Predicate[] temp = new Predicate[2];
                predicateList.add(criteriaBuilder.or(or.toArray(temp)));
            }
            Predicate[] predicates = new Predicate[predicateList.size()];
            criteriaQuery.where(predicateList.toArray(predicates));
            criteriaQuery.orderBy(criteriaBuilder.desc(root.get("createdAt")));
            return criteriaQuery.getRestriction();
        });
    }


    public AjaxResult withdraws(TransactionReq req) {
        Page<ViewTronTransactionWithdraw> all = viewTronTransactionWithdrawRepository.findAll(new Specification<ViewTronTransactionWithdraw>() {
            List<Predicate> predicateList = new ArrayList<>();
            @Override
            public Predicate toPredicate(Root<ViewTronTransactionWithdraw> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicateList = new ArrayList<>();
                Long uid = req.getUid();
                Long topUid = req.getTopUid();
                if(uid != null) {
                    predicateList.add(criteriaBuilder.equal(root.get("uid"), uid));
                }
                if(topUid != null) {
                    predicateList.add(criteriaBuilder.equal(root.get("topUid"), topUid));
                }
                Predicate[] predicates = new Predicate[predicateList.size()];
                criteriaQuery.where(predicateList.toArray(predicates));
                criteriaQuery.orderBy(criteriaBuilder.desc(root.get("createdAt")));
                return criteriaQuery.getRestriction();
            }
        }, req.toPage());
        return AjaxResult.success(all);
    }


    public AjaxResult recharges(TransactionReq req) {
        Page<ViewTronTransactionRecharge> all = viewTronTransactionRechargeRepository.findAll(new Specification<ViewTronTransactionRecharge>() {
            List<Predicate> predicateList = new ArrayList<>();
            @Override
            public Predicate toPredicate(Root<ViewTronTransactionRecharge> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicateList = new ArrayList<>();
                Long uid = req.getUid();
                Long topUid = req.getTopUid();
                Boolean score = req.getScore();
                Integer status = req.getStatus();
                if(uid!=null) {
                    predicateList.add(criteriaBuilder.equal(root.get("uid"), uid));
                }
                if(topUid!=null) {
                    predicateList.add(criteriaBuilder.equal(root.get("topUid"), topUid));
                }
                if(score != null) {
                    predicateList.add(criteriaBuilder.equal(root.get("score"), score));
                }
                if(status != null && status == 5) {
                    predicateList.add(criteriaBuilder.equal(root.get("status"), 3));
                    predicateList.add(criteriaBuilder.equal(root.get("status"), 5));
                }
                if(status != null && status == 4) {
                    predicateList.add(criteriaBuilder.equal(root.get("status"), 2));
                    predicateList.add(criteriaBuilder.equal(root.get("status"), 4));
                }
                Predicate[] predicates = new Predicate[predicateList.size()];
                criteriaQuery.where(predicateList.toArray(predicates));
                criteriaQuery.orderBy(criteriaBuilder.desc(root.get("createdAt")));
                return criteriaQuery.getRestriction();
            }
        }, req.toPage());
        return AjaxResult.success(all);
    }

    public AjaxResult collect(TransactionReq req) {
        Integer rowId = req.getRowId();
        Optional<Transaction> byId = transactionRepository.findById(rowId);
        if(byId.isPresent()){
            Transaction transaction = byId.get();
            transaction.setStatus(2);//待归集
            transactionRepository.save(transaction);
        }
        return AjaxResult.success();
    }
}

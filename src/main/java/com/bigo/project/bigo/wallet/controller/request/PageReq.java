package com.bigo.project.bigo.wallet.controller.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;

@Data
public class PageReq {
    Integer pageSize;
    Integer pageNo;
    String symbol;
    Integer unit;
    Long period;
    Boolean handleFlag;
    Long uid;
    Long agentId;
    String username;
    Long topUid;
    Boolean filter;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    LocalDateTime startTime;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    LocalDateTime endTime;
    private Integer offset;

    public Integer getOffset() {
        return Optional.ofNullable(pageNo).orElse(1)-1;
    }

    public int getPageNo(){
        return Optional.ofNullable(pageNo).orElse(1);
    }

    public Pageable toPage(){
        return PageRequest.of(Optional.ofNullable(pageNo).orElse(1)-1,Optional.ofNullable(pageSize).orElse(20));
    }
}

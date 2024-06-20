package com.bigo.project.bigo.loans.dto;

import lombok.Data;

@Data
public class LoansCheckDTO {
    private Long[] ids;

    private Long googleCaptcha;

    private Integer status;
}

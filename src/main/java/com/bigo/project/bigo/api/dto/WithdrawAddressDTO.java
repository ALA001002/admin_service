package com.bigo.project.bigo.api.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class WithdrawAddressDTO {

    private String coin;

    @NotBlank(message = "to_address_is_not_exist")
    private String address;

    private String remark;
}

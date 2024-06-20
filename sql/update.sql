ALTER TABLE `sys_user`
    ADD COLUMN `google_auth_status`  tinyint NULL DEFAULT 0 COMMENT '谷歌验证器状态：0未绑定，1绑定' AFTER `remark`,
ADD COLUMN `google_auth_secret_key`  varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '谷歌验证器key' AFTER `google_auth_status`;

===========20220423====================
ALTER TABLE `t_withdraw`
    ADD COLUMN `ip_address`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'ip地址' AFTER `transaction_id`,
ADD COLUMN `position`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '位置' AFTER `ip_address`,
ADD COLUMN `error`  varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '失败原因' AFTER `position`;

===========20220424====================
ALTER TABLE `bg_time_period`
    ADD COLUMN `min_money`  decimal(10,2) NULL COMMENT '最低下单金额' AFTER `status`;

===========20220428====================
ALTER TABLE `t_withdraw`
    ADD COLUMN `photo`  varchar(1024) NULL COMMENT '充值凭证' AFTER `error`;

===========20220429====================
ALTER TABLE `bg_config_setting`
DROP COLUMN `ethRechargeMin`,
DROP COLUMN `internalTransferStatus`,
DROP COLUMN `btcBicaiStatus`,
DROP COLUMN `ethBicaiStatus`,
DROP COLUMN `ethErcRecharge`,
DROP COLUMN `btcRecharge`,
DROP COLUMN `ethManualRecharge`,
DROP COLUMN `btcManualRecharge`,
DROP COLUMN `usdtTrcRecharge`,
DROP COLUMN `vipStatus`,
DROP COLUMN `welfareStatus`,
DROP COLUMN `btcRechargeMin`,
DROP COLUMN `ethWithdrawMin`,
DROP COLUMN `btcWithdrawMin`;
CHANGE COLUMN `usdtErcRecharge` `usdtRecharge`  tinyint NULL DEFAULT 0 COMMENT 'usdt充值状态：0-false，1-true' AFTER `mostRebateLevel`;

===========20220430====================
ALTER TABLE `bg_config_setting`
    ADD COLUMN `timeContractStatus`  tinyint NULL DEFAULT 0 COMMENT '期权下单状态' AFTER `loanStatus`;

===========20221216====================
ALTER TABLE `bg_config_setting`
ADD COLUMN `ethManualRecharge`  tinyint NULL DEFAULT 0 COMMENT 'eth人工充值状态' AFTER `timeContractStatus`,
ADD COLUMN `btcManualRecharge`  tinyint NULL DEFAULT 0 COMMENT 'btc注册状态' AFTER `ethManualRecharge`;
    MODIFY COLUMN `ethManualRecharge`  tinyint NULL DEFAULT 0 COMMENT 'eth人工充值状态' AFTER `usdtManualRecharge`,
    MODIFY COLUMN `btcManualRecharge`  tinyint NULL DEFAULT 0 COMMENT 'btc注册状态' AFTER `ethManualRecharge`;

===========20221220====================
ALTER TABLE `bg_user`
    ADD COLUMN `country`  varchar(255) NULL COMMENT '国家' AFTER `withdraw_status`,
ADD COLUMN `driver_license`  varchar(255) NULL COMMENT '驾驶证' AFTER `country`;

ALTER TABLE `bg_user`
    ADD COLUMN `auth_type`  tinyint NULL DEFAULT 1 COMMENT '证件照类型：1-身份证，2-护照，3-驾驶证' AFTER `driver_license`;


===========20230308====================
ALTER TABLE `bg_config_setting`
    ADD COLUMN `tradeFeeRebate`  int NULL DEFAULT 0 COMMENT '交易手续费(%)' AFTER `timeContractStatus`;

===========20230512====================
ALTER TABLE `bg_config_setting`
    ADD COLUMN `probabilityRebate`  decimal(10,2) NULL COMMENT '中签概率' AFTER `tradeFeeRebate`;















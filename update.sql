ALTER TABLE `bg_winning_record`
ADD COLUMN `lottery_code`  varchar(12) NULL COMMENT '抽奖码' AFTER `num`;


ALTER TABLE `bg_kline`
    MODIFY COLUMN `amount`  decimal(28,8) NULL DEFAULT NULL COMMENT '以基础币种计量的交易量' AFTER `timestamp`,
    MODIFY COLUMN `open`  decimal(28,8) NULL DEFAULT NULL COMMENT '本阶段开盘价' AFTER `count`,
    MODIFY COLUMN `close`  decimal(28,8) NULL DEFAULT NULL COMMENT '本阶段收盘价' AFTER `open`,
    MODIFY COLUMN `low`  decimal(28,8) NULL DEFAULT NULL COMMENT '本阶段最低价' AFTER `close`,
    MODIFY COLUMN `high`  decimal(28,8) NULL DEFAULT NULL COMMENT '本阶段最高价' AFTER `low`,
    MODIFY COLUMN `real_open`  decimal(28,8) NULL DEFAULT NULL COMMENT '本阶段真实开盘价' AFTER `vol`,
    MODIFY COLUMN `real_close`  decimal(28,8) NULL DEFAULT NULL COMMENT '本阶段真实收盘价' AFTER `real_open`,
    MODIFY COLUMN `real_low`  decimal(28,8) NULL DEFAULT NULL COMMENT '本阶段真实最低价' AFTER `real_close`,
    MODIFY COLUMN `real_high`  decimal(28,8) NULL DEFAULT NULL COMMENT '本阶段真实最高价' AFTER `real_low`;

-==================添加支付类型字段==================
ALTER TABLE `bg_otc_order`
    ADD COLUMN `pay_type`  tinyint(2) NULL DEFAULT 1 COMMENT '1-银行卡，2-MoonPay' AFTER `payee`;




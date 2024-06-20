CREATE TABLE `bg_user_day_balance` (
   `id` bigint NOT NULL AUTO_INCREMENT,
   `user_id` bigint NOT NULL,
   `balance` decimal(20,8) NOT NULL COMMENT '折合金额',
   `day_no` int NOT NULL COMMENT '日期',
   `create_time` datetime DEFAULT NULL COMMENT '创建时间',
   PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

package com.niewj.entity;

import lombok.Data;

@Data
public class Order {
//  `oid` bigint(20) NOT NULL,
//  `oname` varchar(50) NOT NULL,
//  `user_id` bigint(20) NOT NULL,
//  `ostatus` varchar(10) NOT NULL,
    private Long oid;
    private String oname;
    private Long userId;
    private String ostatus;
}

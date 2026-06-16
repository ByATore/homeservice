package com.homeservice.order.entity.vo;

import com.homeservice.order.entity.Order;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
public class OrderVO extends Order {

    private String userRealName;

    private String userPhone;

    private String workerRealName;

    private String workerPhone;

    private String serviceItemName;

    private String serviceItemUnit;

    private String categoryName;

    private BigDecimal workerRating;

    private Integer workerServiceCount;
}
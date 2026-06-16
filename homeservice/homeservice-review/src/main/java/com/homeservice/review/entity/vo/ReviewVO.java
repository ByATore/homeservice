package com.homeservice.review.entity.vo;

import com.homeservice.review.entity.Review;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ReviewVO extends Review {

    private String userRealName;

    private String workerRealName;

    private String serviceName;
}
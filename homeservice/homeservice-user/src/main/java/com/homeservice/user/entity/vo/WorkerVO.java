package com.homeservice.user.entity.vo;

import com.homeservice.user.entity.Worker;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class WorkerVO extends Worker {

    private String userRealName;

    private String userPhone;

    private String userAvatar;

    private Integer userGender;
}
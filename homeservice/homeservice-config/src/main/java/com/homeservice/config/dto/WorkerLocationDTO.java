package com.homeservice.config.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WorkerLocationDTO {

    @NotNull(message = "服务人员ID不能为空")
    private Long workerId;

    @NotNull(message = "纬度不能为空")
    private Double latitude;

    @NotNull(message = "经度不能为空")
    private Double longitude;
}

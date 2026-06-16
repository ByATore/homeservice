package com.homeservice.config.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkerLocationVO {

    private Long workerId;

    private Double latitude;

    private Double longitude;

    /** 距离（米） */
    private Double distance;

    /** 距离文本 */
    private String distanceText;

    /** 位置更新时间戳 */
    private Long updateTime;
}

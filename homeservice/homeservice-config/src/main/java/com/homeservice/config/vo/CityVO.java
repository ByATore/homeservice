package com.homeservice.config.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CityVO {

    /** 城市编码 */
    private String code;

    /** 城市名称 */
    private String name;

    /** 省名称 */
    private String province;

    /** 首字母 */
    private String initial;
}

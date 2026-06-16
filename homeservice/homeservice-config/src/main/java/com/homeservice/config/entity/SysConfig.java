package com.homeservice.config.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.homeservice.config.constant.ConfigType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_config")
public class SysConfig {

    @TableId(type = IdType.AUTO)
    @Schema(description = "配置ID")
    private Long id;

    @Schema(description = "配置键")
    private String configKey;

    @Schema(description = "配置名称")
    private String configName;

    @Schema(description = "配置值")
    private String configValue;

    @Schema(description = "配置类型：1-文本，2-JSON，3-数字，4-布尔")
    private ConfigType configType;

    @Schema(description = "配置分组")
    private String configGroup;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "状态：0-禁用，1-正常")
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}
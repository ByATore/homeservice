package com.homeservice.media.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("banner")
public class Banner {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 标题 */
    private String title;

    /** 副标题 */
    private String subtitle;

    /** 描述文字 */
    private String description;

    /** 图片地址 */
    private String imageUrl;

    /** 关联 media_file.id */
    private Long imageFileId;

    /** 跳转类型：1=服务详情 2=外部链接 3=分类页 4=预约页 5=不跳转 */
    private Integer linkType;

    /** 跳转目标值 */
    private String linkValue;

    /** 展示位置：home=首页 */
    private String position;

    /** 排序（越小越靠前） */
    private Integer sort;

    /** 状态：0=禁用 1=启用 */
    private Integer status;

    /** 投放开始时间 */
    private LocalDateTime startTime;

    /** 投放结束时间 */
    private LocalDateTime endTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}

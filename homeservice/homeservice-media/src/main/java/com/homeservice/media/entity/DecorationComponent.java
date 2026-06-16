package com.homeservice.media.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("decoration_component")
public class DecorationComponent {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 组件名称 */
    private String name;

    /** 页面: home/category/profile */
    private String page;

    /** 区块: top_banner/hot_service/icon_nav/coupon_bar */
    private String section;

    /** 组件类型: banner/service_card/icon_grid/text_block */
    private String componentType;

    /** 组件展示标题 */
    private String title;

    /** 组件配置数据（JSON） */
    private String configJson;

    /** 配图 */
    private String imageUrl;

    /** 关联 media_file.id */
    private Long imageFileId;

    /** 跳转类型 */
    private Integer linkType;

    /** 跳转目标 */
    private String linkValue;

    /** 同 section 内排序 */
    private Integer sort;

    /** 状态：0=禁用 1=启用 */
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}

package com.homeservice.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user_favorite")
public class UserFavorite {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 服务项目ID */
    private Long serviceItemId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}

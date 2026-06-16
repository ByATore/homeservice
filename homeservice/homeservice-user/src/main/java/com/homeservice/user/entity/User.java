package com.homeservice.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("`user`")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String username;
    
    private String password;
    
    private String phone;
    
    private String email;
    
    private String avatar;
    
    private String realName;
    
    private Integer gender;
    
    private LocalDate birthday;
    
    private String address;
    
    private Integer status;
    
    private Integer userType;
    
    private BigDecimal balance;

    /** 微信openid */
    private String openid;

    /** 微信昵称 */
    private String nickname;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
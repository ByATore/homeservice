package com.homeservice.media.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("media_file")
public class MediaFile {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 原始文件名 */
    private String originalName;

    /** 存储Key/路径 */
    private String fileKey;

    /** 访问URL */
    private String fileUrl;

    /** 缩略图URL */
    private String thumbnailUrl;

    /** 文件类型: image/video/file */
    private String fileType;

    /** MIME类型 */
    private String mimeType;

    /** 文件大小(字节) */
    private Long fileSize;

    /** 图片宽度(px) */
    private Integer width;

    /** 图片高度(px) */
    private Integer height;

    /** 存储类型: local/oss/cos/minio */
    private String storageType;

    /** 关联模块: banner/service/avatar/decoration */
    private String module;

    /** 关联业务ID */
    private Long moduleId;

    /** 上传者ID */
    private Long uploaderId;

    /** 状态：0=已删除 1=正常 */
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}

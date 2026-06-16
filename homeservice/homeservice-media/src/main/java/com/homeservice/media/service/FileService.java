package com.homeservice.media.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.homeservice.media.entity.MediaFile;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件管理服务接口
 */
public interface FileService extends IService<MediaFile> {

    /**
     * 上传单个文件
     */
    MediaFile upload(MultipartFile file, String module, Long moduleId, Long uploaderId);

    /**
     * 删除文件（软删除）
     */
    boolean softDelete(Long fileId);

    /**
     * 根据模块和模块ID查询文件列表
     */
    Page<MediaFile> getFilesByModule(String module, Long moduleId, Integer current, Integer size);
}

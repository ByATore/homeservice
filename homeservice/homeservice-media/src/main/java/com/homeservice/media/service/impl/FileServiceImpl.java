package com.homeservice.media.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.homeservice.media.entity.MediaFile;
import com.homeservice.media.mapper.MediaFileMapper;
import com.homeservice.media.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 文件管理服务实现
 * <p>
 * 默认使用本地存储。可通过 sys_config 配置切换到 OSS/COS/MinIO。
 */
@Slf4j
@Service
public class FileServiceImpl extends ServiceImpl<MediaFileMapper, MediaFile> implements FileService {

    @Value("${spring.servlet.multipart.max-file-size:10MB}")
    private String maxFileSize;

    /** 本地存储根路径 */
    private static final String UPLOAD_ROOT = "uploads";

    @Override
    public MediaFile upload(MultipartFile file, String module, Long moduleId, Long uploaderId) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("上传文件不能为空");
        }

        String originalName = file.getOriginalFilename();
        String extension = "";
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf("."));
        }

        // 生成存储 key
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String fileKey = StrUtil.format("{}/{}/{}{}", module != null ? module : "common",
                datePath, IdUtil.fastSimpleUUID(), extension);

        // 本地存储
        File destFile = new File(UPLOAD_ROOT, fileKey);
        if (!destFile.getParentFile().exists()) {
            destFile.getParentFile().mkdirs();
        }

        try {
            file.transferTo(destFile);
        } catch (IOException e) {
            log.error(String.format("文件存储失败: %s", fileKey), e);
            throw new RuntimeException("文件存储失败");
        }

        // 保存元数据
        MediaFile mediaFile = new MediaFile();
        mediaFile.setOriginalName(originalName);
        mediaFile.setFileKey(fileKey);
        mediaFile.setFileUrl("/uploads/" + fileKey);
        mediaFile.setFileType(getFileType(extension));
        mediaFile.setMimeType(file.getContentType());
        mediaFile.setFileSize(file.getSize());
        mediaFile.setStorageType("local");
        mediaFile.setModule(module);
        mediaFile.setModuleId(moduleId);
        mediaFile.setUploaderId(uploaderId);
        mediaFile.setStatus(1);

        save(mediaFile);
        log.info(String.format("文件上传成功: id=%s, key=%s, size=%s", mediaFile.getId(), fileKey, file.getSize()));
        return mediaFile;
    }

    @Override
    public boolean softDelete(Long fileId) {
        MediaFile mediaFile = new MediaFile();
        mediaFile.setId(fileId);
        mediaFile.setStatus(0); // 软删除
        return updateById(mediaFile);
    }

    @Override
    public Page<MediaFile> getFilesByModule(String module, Long moduleId, Integer current, Integer size) {
        Page<MediaFile> page = new Page<>(current, size);
        LambdaQueryWrapper<MediaFile> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MediaFile::getStatus, 1);
        if (StrUtil.isNotBlank(module)) {
            wrapper.eq(MediaFile::getModule, module);
        }
        if (moduleId != null) {
            wrapper.eq(MediaFile::getModuleId, moduleId);
        }
        wrapper.orderByDesc(MediaFile::getCreatedAt);
        return page(page, wrapper);
    }

    private String getFileType(String extension) {
        if (extension == null) return "file";
        String ext = extension.toLowerCase();
        if (ext.matches("\\.(jpg|jpeg|png|gif|webp|bmp|svg)")) return "image";
        if (ext.matches("\\.(mp4|avi|mov|wmv|flv|mkv)")) return "video";
        return "file";
    }
}
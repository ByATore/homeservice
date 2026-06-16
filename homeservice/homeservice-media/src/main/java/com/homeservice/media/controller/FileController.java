package com.homeservice.media.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.homeservice.common.result.Result;
import com.homeservice.media.entity.MediaFile;
import com.homeservice.media.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "文件管理")
@RestController
@RequestMapping("/media/file")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @Operation(summary = "上传单个文件")
    @PostMapping("/upload")
    public Result<MediaFile> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "common") String module,
            @RequestParam(required = false) Long moduleId,
            @RequestParam(required = false) Long uploaderId) {
        MediaFile mediaFile = fileService.upload(file, module, moduleId, uploaderId);
        return Result.success(mediaFile);
    }

    @Operation(summary = "批量上传文件")
    @PostMapping("/upload/batch")
    public Result<List<MediaFile>> uploadBatch(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam(defaultValue = "common") String module,
            @RequestParam(required = false) Long moduleId,
            @RequestParam(required = false) Long uploaderId) {
        List<MediaFile> result = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                result.add(fileService.upload(file, module, moduleId, uploaderId));
            } catch (Exception e) {
                // 单个文件失败不影响其他文件
            }
        }
        return Result.success(result);
    }

    @Operation(summary = "根据ID获取文件信息")
    @GetMapping("/{id}")
    public Result<MediaFile> getFileById(@PathVariable Long id) {
        MediaFile file = fileService.getById(id);
        return file != null ? Result.success(file) : Result.error("文件不存在");
    }

    @Operation(summary = "分页查询文件列表")
    @GetMapping("/page")
    public Result<Page<MediaFile>> getFilePage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) Long moduleId) {
        return Result.success(fileService.getFilesByModule(module, moduleId, current, size));
    }

    @Operation(summary = "删除文件（软删除）")
    @DeleteMapping("/{id}")
    public Result<Void> deleteFile(@PathVariable Long id) {
        boolean deleted = fileService.softDelete(id);
        return deleted ? Result.success() : Result.error("删除失败");
    }
}

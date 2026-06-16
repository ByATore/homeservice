package com.homeservice.service.controller;

import com.homeservice.common.result.Result;
import com.homeservice.common.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "文件管理")
@RestController
@RequestMapping("/service/file")
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService fileStorageService;

    @Operation(summary = "上传文件")
    @PostMapping("/upload")
    public Result<String> uploadFile(
            @Parameter(description = "文件") @RequestParam("file") MultipartFile file,
            @Parameter(description = "目录") @RequestParam(defaultValue = "common") String directory) {
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }
        String objectName = fileStorageService.uploadFile(file, directory);
        String url = fileStorageService.getFileUrl(objectName);
        return Result.success(url);
    }
}
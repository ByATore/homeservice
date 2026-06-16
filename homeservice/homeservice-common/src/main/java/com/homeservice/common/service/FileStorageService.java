package com.homeservice.common.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    String uploadFile(MultipartFile file, String directory);

    void deleteFile(String objectName);

    String getFileUrl(String objectName);
}
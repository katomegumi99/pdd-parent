package com.pdd.product.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {
    // 图片上传
    String uploadFile(MultipartFile file);
}

package com.pdd.product.controller;

import com.pdd.common.result.Result;
import com.pdd.product.service.FileUploadService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author youzairichangdawang
 * @version 1.0
 */
@Api(tags = "文件上传接口")
@RestController
@RequestMapping("/admin/product")
//
public class FileUploadController {

    @Autowired
    private FileUploadService fileUploadService;

    @ApiOperation("图片上传")
    @PostMapping("fileUpload")
    public Result fileUpload(MultipartFile file) {
        String url = fileUploadService.uploadFile(file);
        return Result.ok(url);
    }
}

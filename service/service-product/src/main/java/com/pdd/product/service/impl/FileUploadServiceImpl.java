package com.pdd.product.service.impl;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.pdd.product.service.FileUploadService;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * @author youzairichangdawang
 * @version 1.0
 */
@Service
public class FileUploadServiceImpl implements FileUploadService {

    @Value("${aliyun.endpoint}")
    private String endPoint;
    @Value("${aliyun.keyid}")
    private String accessKey;
    @Value("${aliyun.keysecret}")
    private String secreKey;
    @Value("${aliyun.bucketname}")
    private String bucketName;

    //图片上传
    @Override
    public String uploadFile(MultipartFile file) {
        try {
            // 1 创建 OSSClient 实例
            OSS ossClient = new OSSClientBuilder().build(endPoint, accessKey, secreKey);

            // 2 创建上传文件流
            InputStream inputStream = file.getInputStream();
            String fileName = file.getOriginalFilename();

            // 3 使用uuid生成随机唯一值并将uuid中的 - 去掉，添加到文件名称中去
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            fileName = uuid + fileName;

            // 4 按照当前日期，创建文件夹，将上传的文件按日期归类
            String currentTime = new DateTime().toString("yyyy/MM/dd");
            fileName = currentTime + "/" + fileName;

            // 5 调用方法实现上传
            ossClient.putObject(bucketName, fileName, inputStream);

            // 6 关闭 OSSClient 实例
            ossClient.shutdown();

            // 7 返回上传之后得到的文件路径
            String url = "https://"+bucketName+"."+endPoint+"/"+fileName;
            return url;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

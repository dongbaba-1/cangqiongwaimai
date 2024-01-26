package com.sky.controller.admin;

import com.sky.properties.AliOssProperties;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
@Api(tags = "公共接口")
@RequestMapping("/admin/common")
public class CommonController {


    @Autowired
    private AliOssProperties aliOssProperties;

    @Autowired
    private AliOssUtil aliOssUtil;
    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public Result<String> upload(MultipartFile file) throws Exception {
        log.info("文件上传：{}",file);
//        AliOssUtil aliOssUtil = new AliOssUtil(aliOssProperties.getEndpoint(),aliOssProperties.getAccessKeyId()
//        ,aliOssProperties.getAccessKeySecret(), aliOssProperties.getBucketName());

        try {
            //获取原始文件名
            String originalFilename = file.getOriginalFilename();
            String extension = null;
            //截取原始文件名后缀.jpg .png
            if (originalFilename != null) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            //使用UUID构造上传到阿里云的文件名
            String objectName = UUID.randomUUID().toString() + extension;
            //文件的请求路径
            String filePath = aliOssUtil.upload(file.getBytes(),objectName);
            return Result.success(filePath);//把请求路径返回给前端让前端能访问aliyun中的地址并显示图片
        } catch (IOException e) {
            log.error("文件上传失败");
        }

        return Result.error("文件上传失败");
    }
}

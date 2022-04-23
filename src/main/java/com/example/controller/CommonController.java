package com.example.controller;

import com.example.common.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件上传和下载
 * @author Blockbuster
 * @date 2022/4/19 14:07:10 星期二
 */

@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @Value("${reggie.path}")
    private String basePath;
    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    // file 名字不能改，和前端保持一致，file 是临时文件，需要转存到其他地方
    public R<String> upload(MultipartFile file){

        // 获取文件原始名
        String originalFilename = file.getOriginalFilename();

        // 获取原始文件名后缀，即文件后缀名 .xxx
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));


        // 使用UUID重新生成文件名，防止文件重名，拼接文件后缀名
        String fileName = UUID.randomUUID() + suffix;

        // 创建一个目录对象
        File dir = new File(basePath);

        // 判断路径目录是否存在，不存在则创建文件夹
        if (!dir.exists()){
            dir.mkdirs();
        }

        // 文件转存
        try {
            // 文件路径为 yml 里自定义路径 + UUID（即文件名）
            file.transferTo(new File(basePath + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return R.success(fileName);
    }

    /**
     * 文件下载
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) {

        // 获取文件输入流 和 浏览器响应的输出流 ServletOutputStream 用 try-with-resource 写法 不用手动 close
        try(FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));
            ServletOutputStream outputStream = response.getOutputStream()) {

            // 用 common-io 包的工具类 IOUtil 的复制功能
            IOUtil.copy(fileInputStream, outputStream);

            // 设定格式
            response.setContentType("image/jpeg");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}

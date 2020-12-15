package com.mr.service;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class UploadService {
    @Autowired
    private FastFileStorageClient storageClient;


    public String uploadImg(MultipartFile file) throws IOException {



        if(file.getSize() == 0){
//            throw new MrException(ExceptionEnums.UPLOAD_SIZE_NULL);
            System.out.println("文件大小错误!!!");
        }


        //获取文件名
        String  fileName =  file.getOriginalFilename();
        //获取文件扩展名                         开始位置截取                        结束位置
        String fileExt =  fileName.substring(fileName.lastIndexOf(".")+1,fileName.length());
        //将扩展名转换成小写
        fileExt =  fileExt.toLowerCase();

        // 校验上传内容是否是图片
        if(!"jpg".equals(fileExt) && !"jpeg".equals(fileExt) &&!"png".equals(fileExt) &&!"bmp".equals(fileExt)
                && !"gif".equals(fileExt)){
//            throw new MrException(ExceptionEnums.UPLOAD_TYPE_ERROR);
            System.out.println("文件类型错误!!!");
        }


        //文件名称
        String imageName =  UUID.randomUUID()+"."+fileExt;

        String newImageImg = "";

        //原图上传
        //StorePath storePath = storageClient.uploadFile(file.getInputStream(), file.getSize(), fileExt, null);
//        上传原图与缩略图
        StorePath storePath = storageClient.uploadImageAndCrtThumbImage(file.getInputStream(), file.getSize(), fileExt, null);
        System.out.println(storePath.getFullPath());
        System.out.println(storePath.getGroup());
        System.out.println(storePath.getPath());
        newImageImg = storePath.getFullPath();

        return "http://image.b2c.com/"+newImageImg; //FastDFS
    }


}

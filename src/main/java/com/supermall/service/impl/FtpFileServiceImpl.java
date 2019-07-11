package com.supermall.service.impl;

import com.google.common.collect.Lists;
import com.supermall.service.IFtpFileService;
import com.supermall.util.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * @author
 */
@Service("iFtpFileService")
public class FtpFileServiceImpl implements IFtpFileService {


    private Logger logger = LoggerFactory.getLogger(FtpFileServiceImpl.class);


    /**
     * 将上传的文件名返回
     * @param file
     * @param path
     * @return
     */
    @Override
    public String upload(MultipartFile file,String path){
        //原文件名--整个名称
        String fileName = file.getOriginalFilename();
        //获取文件的扩展名   从文件名的后面获取
        String fileExtensionName = fileName.substring(fileName.lastIndexOf("."));
        //设置上传文件的名字
        String uploadFileName = UUID.randomUUID().toString()  + fileExtensionName;

        logger.info("开始上传文件，上传文件的文件名:{},上传的路径:{},新文件名:{}",fileName,path,uploadFileName);

        //新的文件夹目录
        File fileDir = new File(path);
        //判断是否存在
        if (!fileDir.exists()){
            //可写
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }
        //创建目录下的文件
        File targetFile = new File(path,uploadFileName);

        //上传文件
        try {

            file.transferTo(targetFile);
            //到现在为止，文件已经上传成功
            //将文件上传到ftp服务器上
            List<File> targetFileList = Lists.newArrayList(targetFile);
            FTPUtil.uploadFile(targetFileList);
            //上传完之后，还要将upload下的文件删除
            targetFile.delete();

        } catch (IOException e) {
            logger.error("上传文件异常",e);
            return null;
        }
        //将目标文件的文件名上传
        return targetFile.getName();
    }


}

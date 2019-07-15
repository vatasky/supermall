package com.supermall.util;

import org.apache.commons.io.FilenameUtils;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;


/**
 * @author vatasky
 */
public class FastDFSUtil {
    private static Logger logger = LoggerFactory.getLogger(FastDFSUtil.class);

    //图片服务器
    public static final String IMG_URL = "http://114.115.206.170/";
    /**
           * @Description: 上传图片到Fast.
           * @Title: uploadPic
           * @author:  xuyou
           * @date:   2017年12月14日
           * @param pic 图片二进制
           * @param name 图片名称
           * @param size 图片大小
           * @return
           */
     public static String uploadPic(byte[] pic ,String name,long size){
                 String path = null;

                 try {
                        //ClientGloble 读配置文件
                         ClassPathResource resource = new ClassPathResource("fastdfs.properties");
                         ClientGlobal.initByProperties(resource.getClassLoader().getResource("fastdfs.properties").toURI().getPath());
                         //链接FastDFS服务器
                         TrackerClient trackerClient = new TrackerClient();
                         TrackerServer trackerServer = trackerClient.getConnection();
                         logger.info("链接调度服务器成功");

                         StorageServer storageServer  = null;
                         StorageClient1 storageClient1 = new StorageClient1(trackerServer, storageServer);
                         logger.info("链接存储服务器成功");

                         //图片11.jpg  根据图片名称得到图片后缀    jpg
                         //获取图片的扩展名
                         String ext = FilenameUtils.getExtension(name);

                         NameValuePair[] meta_list = new NameValuePair[3];
                         meta_list[0] = new NameValuePair("fileName",name);
                         meta_list[1] = new NameValuePair("fileExt",ext);
                         meta_list[2] = new NameValuePair("fileSize",String.valueOf(size));


                         //  group1/M00/00/01/wKjIgFWOYc6APpjAAAD-qk29i78248.jpg
                         path = storageClient1.upload_file1(pic, ext, meta_list);
                        } catch (Exception e) {
                         // TODO Auto-generated catch block
                         e.printStackTrace();
                     }
                 return path;
             }
}

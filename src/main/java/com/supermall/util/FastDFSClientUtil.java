package com.supermall.util;

import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;

import java.io.BufferedOutputStream;
import java.io.IOException;

/**
 * @author vatasky
 * @使用示例 
 *
 * <li>2.创建FastDfs客户端并传入参数classpath:fastdfs.properties(图片服务器配置文件地址),
 * FastDFSClientUtil client = new FastDFSClientUtil("fastdfs.properties");</li>
 * <li>3.使用客户端调用相关方法
 * String url = client.uploadFile(fileContent, extName);
 * url为返回路径,客户端访问的时候可以在路径前拼接图片服务器的地址</li>
 */
public class FastDFSClientUtil {
    private TrackerClient trackerClient = null;
    private TrackerServer trackerServer = null;
    private StorageServer storageServer = null;
    private StorageClient1 storageClient = null;

    public FastDFSClientUtil(String prop) throws Exception {
        if (prop.contains("classpath:")) {
            prop = prop.replace("classpath:", this.getClass().getResource("/").getPath());
        }
        ClientGlobal.init(prop);
        trackerClient = new TrackerClient();
        trackerServer = trackerClient.getConnection();
        storageServer = null;
        storageClient = new StorageClient1(trackerServer, storageServer);
    }

    /**
     * 上传文件方法
     * <p>Title: uploadFile</p>
     * <p>Description: </p>
     * @param fileName 文件全路径
     * @param extName 文件扩展名，不包含（.）
     * @param metas 文件扩展信息
     * @return
     * @throws Exception
     */
    public String uploadFile(String fileName, String extName, NameValuePair[] metas) throws Exception {
        String result = storageClient.upload_file1(fileName, extName, metas);
        return result;
    }

    public String uploadFile(String fileName) throws Exception {
        return uploadFile(fileName, null, null);
    }

    public String uploadFile(String fileName, String extName) throws Exception {
        return uploadFile(fileName, extName, null);
    }

    /**
     * 上传文件方法
     * <p>Title: uploadFile</p>
     * <p>Description: </p>
     * @param fileContent 文件的内容，字节数组
     * @param extName 文件扩展名
     * @param metas 文件扩展信息
     * @return
     * @throws Exception
     */
    public String uploadFile(byte[] fileContent, String extName, NameValuePair[] metas) throws Exception {

        String result = storageClient.upload_file1(fileContent, extName, metas);
        return result;
    }

    public String uploadFile(byte[] fileContent) throws Exception {
        return uploadFile(fileContent, null, null);
    }

    public String uploadFile(byte[] fileContent, String extName) throws Exception {
        return uploadFile(fileContent, extName, null);
    }
    /** 
      * 下载文件 
      * @param path 路径地址如：group1/M00/00/00/wKgRsVjtwpSAXGwkAAAweEAzRjw471.jpg
      * @param output 字节输出包装流 
      * @return -1失败,0成功 ,2找不到文件
      */
    public int download_file(String path,BufferedOutputStream output) {
         //byte[] b = storageClient.download_file(group, path);  
        int result=-1;
        try {
         byte[] b = storageClient.download_file1(path);
         System.out.println(b.length);
         try{
        if(b != null){
         output.write(b);
         result=0;
        }
         }catch (Exception e){} //用户可能取消了下载  
         finally {
              if (output != null)
         try {
        output.close();
        } catch (IOException e) {
         e.printStackTrace();
        }
         }
        } catch (Exception e) {
         e.printStackTrace();
         }
         return result;
    }

        /** 
          * 删除文件 
          * @param group 组名 如：group1 
          * @param storagePath 不带组名的路径名称 如：M00/00/00/wKgRsVjtwpSAXGwkAAAweEAzRjw471.jpg 
          * @return -1失败,0成功 ,2找不到文件
          */
     public Integer delete_file(String group ,String storagePath){
        int result=-1;
        try {
        result = storageClient.delete_file(group, storagePath);
         } catch (Exception e) {
         e.printStackTrace();
        }
        return result;
     }
/** 
          *  删除文件
          * @param storagePath  文件的全部路径 如：group1/M00/00/00/wKgRsVjtwpSAXGwkAAAweEAzRjw471.jpg 
          * @return -1失败,0成功 ,2找不到文件
          */
     public Integer delete_file(String storagePath){
         int result=-1;
         try {
         result = storageClient.delete_file1(storagePath);
         } catch (Exception e) {
         e.printStackTrace();
         }
         return result;
     }

}

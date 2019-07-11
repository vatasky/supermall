package com.supermall.util;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class FTPUtil {

    private static Logger logger = LoggerFactory.getLogger(FTPUtil.class);

    private static String ftpIp = PropertiesUtil.getProperty("ftp.server.ip");
    private static String ftpUser = PropertiesUtil.getProperty("ftp.user");
    private static String ftpPass = PropertiesUtil.getProperty("ftp.pass");

    private String ip;
    private int port;
    private String user;
    private String pwd;
    private FTPClient ftpClient;


    public FTPUtil(String ip,int port,String user,String pwd){
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.pwd = pwd;
    }

    /**
     * 批量上传,暴露在外面的就一个uploadFile的方法
     * @param fileList
     * @return
     * @throws IOException
     */
    public static boolean uploadFile(List<File> fileList) throws IOException {
        FTPUtil ftpUtil = new FTPUtil(ftpIp,21,ftpUser,ftpPass);
        logger.info("开始连接ftp服务器");

        boolean result = ftpUtil.uploadFile("img",fileList);
        logger.info("开始连接ftp服务器，结束上传，上传结果:{}",result);
        return result;
    }

    private boolean uploadFile(String remotePath,List<File> fileList) throws IOException {
        boolean uploaded = true;
        FileInputStream fis = null;
        //连接FTP服务器
        if (connectFTPServer(this.ip,this.port,this.user,this.pwd)){
            //更改工作目录,需不需要切换文件夹
            try {
                ftpClient.changeWorkingDirectory(remotePath);
                ftpClient.setBufferSize(1024);
                //设置编码格式
                ftpClient.setControlEncoding("UTF-8");
                //设置成二进制，防止乱码
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                ftpClient.enterLocalPassiveMode();
                for (File fileItem:fileList){
                    fis = new FileInputStream(fileItem);
                    ftpClient.storeFile(fileItem.getName(),fis);
                }


            } catch (IOException e) {
                logger.error("上传文件异常",e);
                uploaded = false;
            }finally {
                //关闭流
                fis.close();
                //断开ftp服务器的连接
                ftpClient.disconnect();
            }
        }
        return uploaded;
    }

    private boolean connectFTPServer(String ip,int port,String user,String pwd){
        //判断连接是否成功
        boolean isSuccess = false;

        ftpClient = new FTPClient();
        try {
            ftpClient.connect(ip);
            isSuccess = ftpClient.login(user,pwd);
        } catch (IOException e) {
            logger.error("连接服务器异常",e);
        }
        return isSuccess;
    }


    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public FTPClient getFtpClient() {
        return ftpClient;
    }

    public void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }
}

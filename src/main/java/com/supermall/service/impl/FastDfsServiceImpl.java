package com.supermall.service.impl;

import com.supermall.common.ServerResponse;
import com.supermall.service.IFastDfsService;
import com.supermall.util.FastDFSUtil;
import org.springframework.stereotype.Service;

/**
 * @author vatasky
 */
@Service("iFastDfsService")
public class FastDfsServiceImpl implements IFastDfsService{
    /**
     * //上传图片
     * @param pic
     * @param name
     * @param size
     * @return
     */
    @Override
     public String uploadPic(byte[] pic , String name, long size){
         return FastDFSUtil.uploadPic(pic, name, size);
     }
}

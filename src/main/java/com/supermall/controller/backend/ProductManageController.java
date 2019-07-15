package com.supermall.controller.backend;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.supermall.common.Const;
import com.supermall.common.ResponseCode;
import com.supermall.common.ServerResponse;
import com.supermall.pojo.Product;
import com.supermall.pojo.User;
import com.supermall.service.IFastDfsService;
import com.supermall.service.IFtpFileService;
import com.supermall.service.IProductService;
import com.supermall.service.IUserService;
import com.supermall.util.FastDFSUtil;
import org.csource.common.MyException;
import org.csource.fastdfs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import sun.security.pkcs11.wrapper.Constants;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * @author
 */
@Controller
@RequestMapping("/manage/product")
public class ProductManageController {

    private Logger logger = LoggerFactory.getLogger(ProductManageController.class);

    @Autowired
    private IUserService iUserService;
    @Autowired
    private IProductService iProductService;
    @Autowired
    private IFtpFileService iFtpFileService;
    @Autowired
    private IFastDfsService iFastDfsService;

    /**
     * 添加或者更新产品
     * @param session
     * @param product
     * @return
     */
    @RequestMapping("addOrUpdateProduct.do")
    @ResponseBody
    public ServerResponse addOrUpdateProduct(HttpSession session,Product product){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，需要管理员登录");
        }
        //判断是否是管理员登录
        ServerResponse response = iUserService.checkAdmin(user);
        if (response.isSuccess()){
            //对产品进行添加或者更新
            return iProductService.addOrUpdateProduct(product);

        }else {
            return ServerResponse.createByErrorMessage("无操作权限");
        }

    }

    /**
     * 设置产品的销售状态，是否下架
     * @param session
     * @param productId
     * @param status
     * @return
     */
    @RequestMapping("setProductSaleStatus.do")
    @ResponseBody
    public ServerResponse setProductSaleStatus(HttpSession session,Integer productId,Integer status){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，需要管理员登录");
        }
        //判断是否是管理员登录
        ServerResponse response = iUserService.checkAdmin(user);
        if (response.isSuccess()){
            //设置产品销售状态
            return iProductService.setProductSaleStatus(productId,status);

        }else {
            return ServerResponse.createByErrorMessage("无操作权限");
        }

    }


    /**
     * 商品详情
     * @param session
     * @param productId
     * @return
     */
    @RequestMapping("getDetail.do")
    @ResponseBody
    public ServerResponse getDetail(HttpSession session,Integer productId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，需要管理员登录");
        }
        //判断是否是管理员登录
        ServerResponse response = iUserService.checkAdmin(user);
        if (response.isSuccess()){
            //查看产品详情
            return iProductService.manageProductDetail(productId);

        }else {
            return ServerResponse.createByErrorMessage("无操作权限");
        }

    }

    /**
     * 获取商品列表
     * 需要进行动态分页，利用pagehelper
     * @param session
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse getList(HttpSession session, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,@RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，需要管理员登录");
        }
        //判断是否是管理员登录
        ServerResponse response = iUserService.checkAdmin(user);
        if (response.isSuccess()){
            //查看产品列表并进行动态分页处理
            return iProductService.getProductList(pageNum,pageSize);

        }else {
            return ServerResponse.createByErrorMessage("无操作权限");
        }

    }

    /**
     * 通过name和 id 来进行
     * @param session
     * @param productName
     * @param productId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse searchProduct(HttpSession session,String productName,Integer productId,@RequestParam(value = "pageNum",defaultValue = "1") int pageNum,@RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，需要管理员登录");
        }
        //判断是否是管理员登录
        ServerResponse response = iUserService.checkAdmin(user);
        if (response.isSuccess()){
            //查看产品列表并进行动态分页处理
            return iProductService.productSerch(productName,productId,pageNum,pageSize);

        }else {
            return ServerResponse.createByErrorMessage("无操作权限");
        }

    }

    /**
     * ftp上传文件  springMvc上传文件
     * RequestParam中的value是对应前端input中的name属性值
     * @param file
     * @param request
     * @return
     */
    /*@RequestMapping("upload.do")
    @ResponseBody
    public ServerResponse upload(HttpSession session,@RequestParam(value = "upload_file",required = false) MultipartFile file, HttpServletRequest request){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，需要管理员登录");
        }
        //判断是否是管理员登录
        ServerResponse response = iUserService.checkAdmin(user);
        if (response.isSuccess()){
            //文件上传内容

            //获取上传文件的路径
            String path = request.getSession().getServletContext().getRealPath("upload");
            //获取文件名
            String targetFileName = iFtpFileService.upload(file,path);
            //上传文件的url
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;
            Map fileMap = Maps.newHashMap();
            fileMap.put("uri",targetFileName);
            fileMap.put("url",url);

            return ServerResponse.createBySuccess(fileMap);

        }else {
            return ServerResponse.createByErrorMessage("无操作权限");
        }


    }*/

    /**
     * 富文本上传文件，此写法只针对simditor富文本编辑器
     * @param session
     * @param file
     * @param request
     * @param response
     * @return
     */
    /*@RequestMapping("richtextImgUpload.do")
    @ResponseBody
    public Map richtextImgUpload(HttpSession session, @RequestParam(value = "upload_file",required = false) MultipartFile file, HttpServletRequest request, HttpServletResponse response){

        Map resultMap = Maps.newHashMap();

        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            resultMap.put("success",false);
            resultMap.put("msg","账号未登录，请登录管理员账号");
            return resultMap;
        }



        //富文本中对于返回值有自己的要求，我们使用的是simditor富文本编辑器，所以按照simditor的要求进行返回,下面是返回格式
        //{
         //   "success":true/false,
          //  "msg":"error message",#optional
          //  "file_path":"[real file path]"
       // }

        //判断是否是管理员登录
        if (iUserService.checkAdmin(user).isSuccess()){
            //文件上传内容

            //获取上传文件的路径
            String path = request.getSession().getServletContext().getRealPath("upload");
            //获取文件名
            String targetFileName = iFtpFileService.upload(file,path);
            if (StringUtils.isBlank(targetFileName)){
                resultMap.put("success",false);
                resultMap.put("msg","上传失败");
                return resultMap;
            }
            //上传文件的url
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;
            resultMap.put("success",true);
            resultMap.put("msg","上传成功");
            resultMap.put("file_path",url);

            //和前端约定一致，需要重新设置请求头,没有约定的话可以不用设置
            response.addHeader("Access-Control-Headers","X-File_Name");

            return resultMap;

        }else {
            resultMap.put("success",false);
            resultMap.put("msg","无权限操作");
            return resultMap;
        }


    }*/


    /**
     * 单张图片上传
     * @RequestParam(required = false 防止为null 抛异常
     * @param pic
     * @param response
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "uploadPic.do")
    @ResponseBody
     public void uploadPic(@RequestParam(value = "upload_file",required = false) MultipartFile pic,HttpServletResponse response) throws IOException{

        byte[] fileContent = pic.getBytes();
        String fileName = pic.getOriginalFilename();
        long size = pic.getSize();

        String path = iFastDfsService.uploadPic(fileContent,fileName, size);
        String url = FastDFSUtil.IMG_URL + path;
        JSONObject jo = new JSONObject();
        jo.put("url", url);

        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(jo.toString());

    }

    /**
     * 多张图片上传
     * @param pics
     * @param response
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "uploadPics.do")
    @ResponseBody
    public List<String> uploadPics(@RequestParam(value = "upload_file",required = false) MultipartFile[] pics,HttpServletResponse response) throws IOException{

        List<String> urls = Lists.newArrayList();

        for (MultipartFile pic : pics) {
            String path = iFastDfsService.uploadPic(pic.getBytes(), pic.getOriginalFilename(), pic.getSize());
            String url = FastDFSUtil.IMG_URL + path;
            urls.add(url);
            }
            return urls;
        }





















}

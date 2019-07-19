<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>supermall</title>
</head>
<body>
<h2>Hello World!</h2>

<%--<p>springMvc上传文件</p>
<form action="/manage/product/upload.do" method="post" enctype="multipart/form-data">
    <input type="file" name="upload_file"/>
    <input type="submit" value="springmvc上传文件"/>
</form>

<p>富文本图片上传</p>
<form action="/manage/product/richtextImgUpload.do" method="post" enctype="multipart/form-data">
    <input type="file" name="upload_file"/>
    <input type="submit" value="富文本编辑器上传文件"/>
</form>--%>

<p>fastdfs服务器上传文件</p>
<form action="/manage/product/uploadPic.do" method="post" enctype="multipart/form-data">
    <input type="file" name="upload_file"/>
    <input type="submit" value="FastDFS上传文件"/>
</form>

<p>fastdfs服务器上传多张图片,因为没有写img标签来接收这个地址，只能返回图片的地址</p>
<form action="/manage/product/uploadPics.do" method="post" enctype="multipart/form-data">
    <input type="file" name="upload_file"/>
    <input type="submit" value="FastDFS上传文件"/>
</form>


<p>喻湘东上传传接口，swagger的api接口，文件服务器是fastdfs搭建的</p>
<form action="https://www.yxdc.xyz/file/upload" method="post" enctype="multipart/form-data">
    <input type="file" name="file"/>
    <input type="submit" value="swagger的api上传文件"/>
</form>



</body>
</html>


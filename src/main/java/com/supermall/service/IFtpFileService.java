package com.supermall.service;

import org.springframework.web.multipart.MultipartFile;

public interface IFtpFileService {

    String upload(MultipartFile file, String path);
}

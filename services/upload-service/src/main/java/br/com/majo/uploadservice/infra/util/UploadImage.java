package br.com.majo.uploadservice.infra.util;

import org.springframework.web.multipart.MultipartFile;

public interface UploadImage {

    String upload(MultipartFile image);

}

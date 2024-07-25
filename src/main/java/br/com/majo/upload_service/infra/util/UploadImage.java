package br.com.majo.upload_service.infra.util;

import org.springframework.web.multipart.MultipartFile;

public interface UploadImage {

    String upload(MultipartFile image);

}

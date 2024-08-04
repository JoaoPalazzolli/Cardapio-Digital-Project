package br.com.majo.uploadservice.services;

import br.com.majo.uploadservice.infra.exceptions.UploadException;
import br.com.majo.uploadservice.infra.util.ImageTypes;
import br.com.majo.uploadservice.infra.util.UploadImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Objects;

@Service
public class AbstractLocalUploadImageService implements UploadImage {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${upload.images.path}")
    private String directory;

    @Override
    public String upload(MultipartFile image) {
        logger.info("Uploading image");

        var storageLocation = createDirectory();

        if(!isValidFileType(image.getContentType())){
            throw new UploadException("Invalid Type");
        }

        try{
            final String filename = StringUtils.cleanPath(Objects.requireNonNull(image.getOriginalFilename()));

            storageLocation = storageLocation.resolve(filename);

            Files.copy(image.getInputStream(), storageLocation, StandardCopyOption.REPLACE_EXISTING);

            return storageLocation.toAbsolutePath().toString();
        } catch (IOException e){
            throw new UploadException("Error uploading image");
        }
    }

    private Path createDirectory(){
        try {
            var imageStorageLocation = Paths.get(directory).toAbsolutePath().normalize();
            Files.createDirectories(imageStorageLocation);

            return imageStorageLocation;
        } catch (IOException e){
            throw new UploadException("Error creating directory");
        }
    }

    private Boolean isValidFileType(String type){
        return Arrays.stream(ImageTypes.values()).anyMatch(x -> type.toUpperCase().contains(x.toString()));
    }
}

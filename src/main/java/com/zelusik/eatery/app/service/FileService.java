package com.zelusik.eatery.app.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.zelusik.eatery.app.dto.file.S3FileDto;
import com.zelusik.eatery.global.exception.ExceptionUtils;
import com.zelusik.eatery.global.exception.file.MultipartFileNotReadableException;
import com.zelusik.eatery.global.log.LogUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class FileService {

    private final AmazonS3Client s3Client;

    @Value("${cloud.aws.s3.bucket-name}")
    private String bucketName;

    @Transactional
    public S3FileDto upload(MultipartFile multipartFile, String dirPath) {
        String originalFilename = multipartFile.getOriginalFilename();
        if (originalFilename == null) {
            originalFilename = "";
        }
        String storeFileName = createStoreFileName(originalFilename, dirPath);
        ObjectMetadata objectMetadata = createMetadataWithContentInfo(multipartFile);

        InputStream inputStream;
        try {
            inputStream = multipartFile.getInputStream();
        } catch (IOException ex) {
            log.info("[{}] !Catch Exception! FileService.save() ex={}", LogUtils.getLogTraceId(), ExceptionUtils.getExceptionStackTrace(ex));
            throw new MultipartFileNotReadableException(ex);
        }

        s3Client.putObject(
                new PutObjectRequest(
                        bucketName,
                        storeFileName,
                        inputStream,
                        objectMetadata
                ).withCannedAcl(CannedAccessControlList.PublicRead)
        );
        String storedFileUrl = s3Client.getResourceUrl(bucketName, storeFileName);

        return S3FileDto.of(originalFilename, storeFileName, storedFileUrl);
    }

    private String createStoreFileName(String originalFilename, String dirPath) {
        // Extract file extension
        int pos = originalFilename.lastIndexOf(".");
        String extension = originalFilename.substring(pos + 1);
        String uuid = UUID.randomUUID().toString();
        return dirPath + uuid + "." + extension;
    }

    private ObjectMetadata createMetadataWithContentInfo(MultipartFile multipartFile) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(multipartFile.getContentType());
        objectMetadata.setContentLength(multipartFile.getSize());
        return objectMetadata;
    }
}

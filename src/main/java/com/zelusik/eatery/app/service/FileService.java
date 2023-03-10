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

    /**
     * MultipartFile을 전달받아 S3 bucket에 업로드한다.
     *
     * @param multipartFile 업로드할 파일
     * @param dirPath       업로드할 경로
     * @return 업로드한 파일 정보
     */
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

    /**
     * S3 Bucket에 업로드할 고유한 파일 이름을 생성한다.
     *
     * @param originalFilename 파일의 원래 이름.
     * @param dirPath          업로드 경로
     * @return 생성된 고유한 파일 이름
     */
    private String createStoreFileName(String originalFilename, String dirPath) {
        // Extract file extension
        int pos = originalFilename.lastIndexOf(".");
        String extension = originalFilename.substring(pos + 1);
        String uuid = UUID.randomUUID().toString();
        return dirPath + uuid + "." + extension;
    }

    /**
     * MultipartFile에 대한 ObjectMetadata를 생성한다.
     *
     * @param multipartFile MultipartFile
     * @return multipartFile에 대해 생성된 ObjectMetadata
     */
    private ObjectMetadata createMetadataWithContentInfo(MultipartFile multipartFile) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(multipartFile.getContentType());
        objectMetadata.setContentLength(multipartFile.getSize());
        return objectMetadata;
    }
}

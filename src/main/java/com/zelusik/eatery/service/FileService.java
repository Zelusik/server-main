package com.zelusik.eatery.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.zelusik.eatery.dto.file.S3FileDto;
import com.zelusik.eatery.util.CustomMultipartFile;
import com.zelusik.eatery.exception.ThumbnailImageCreateException;
import com.zelusik.eatery.exception.file.MultipartFileNotReadableException;
import lombok.RequiredArgsConstructor;
import marvin.image.MarvinImage;
import org.marvinproject.image.transform.scale.Scale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class FileService {

    private static final int THUMBNAIL_IMAGE_WIDTH = 500;

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
    public S3FileDto uploadFile(MultipartFile multipartFile, String dirPath) {
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
     * S3 bucket에 이미지를 업로드한다.
     * 전달받은 multipartFile을 리사이징한 썸네일 이미지를 생성하고, 원본 이미지와 썸네일 이미지 두 개를 모두 업로드한다.
     *
     * @param multipartFile 업로드할 이미지 파일
     * @param dirPath       업로드할 경로
     * @return 업로드된 파일 정보. 원본 이미지와 썸네일 이미지 두 개에 대한 정보가 모두 담겨있다.
     */
    @Transactional
    public S3ImageDto uploadImageWithResizing(MultipartFile multipartFile, String dirPath) {
        MultipartFile resizedImage = resizeImage(multipartFile);

        S3FileDto originalImageDto = this.uploadFile(multipartFile, dirPath);
        S3FileDto thumbnailImageDto = this.uploadFile(resizedImage, dirPath + "thumbnail/");

        return S3ImageDto.of(
                originalImageDto.getOriginalName(),
                originalImageDto.getStoredName(),
                originalImageDto.getUrl(),
                thumbnailImageDto.getStoredName(),
                thumbnailImageDto.getUrl()
        );
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

    /**
     * 이미지를 썸네일용으로 압축(resizing)한다.
     *
     * @param originalImage 원본 이미지 파일
     * @return resizing 된 이미지 파일
     */
    private MultipartFile resizeImage(MultipartFile originalImage) {
        BufferedImage bufferedImage;
        try {
            bufferedImage = ImageIO.read(originalImage.getInputStream());
        } catch (IOException ex) {
            throw new MultipartFileNotReadableException(ex);
        }

        int originalWidth = bufferedImage.getWidth();
        int originalHeight = bufferedImage.getHeight();

        if (originalWidth <= THUMBNAIL_IMAGE_WIDTH) {
            return originalImage;
        }

        String imageFormat;
        if (originalImage.getContentType() == null || originalImage.getContentType().length() == 0) {
            imageFormat = "jpg";
        } else {
            int imageFormatPos = originalImage.getContentType().lastIndexOf("/");
            imageFormat = originalImage.getContentType().substring(imageFormatPos + 1);
        }

        MarvinImage marvinImage = new MarvinImage(bufferedImage);
        Scale scale = new Scale();
        scale.load();
        scale.setAttribute("newWidth", FileService.THUMBNAIL_IMAGE_WIDTH);
        scale.setAttribute("newHeight", FileService.THUMBNAIL_IMAGE_WIDTH * originalHeight / originalWidth);
        scale.process(marvinImage.clone(), marvinImage, null, null, false);

        BufferedImage bufferedImageNoAlpha = marvinImage.getBufferedImageNoAlpha();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImageNoAlpha, imageFormat, byteArrayOutputStream);
            byteArrayOutputStream.flush();
        } catch (IOException ex) {
            throw new ThumbnailImageCreateException(ex);
        }

        return new CustomMultipartFile(
                originalImage.getName(),
                originalImage.getOriginalFilename(),
                originalImage.getContentType(),
                byteArrayOutputStream.toByteArray()
        );
    }
}

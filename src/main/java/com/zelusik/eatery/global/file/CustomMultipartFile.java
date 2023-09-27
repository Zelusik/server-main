package com.zelusik.eatery.global.file;

import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class CustomMultipartFile implements MultipartFile {

    private final String name;
    private String originalFilename;
    private String contentType;
    private final byte[] content;

    public CustomMultipartFile(String name, String originalFilename, String contentType, byte[] content) {
        this.name = name;
        this.originalFilename = originalFilename;
        this.contentType = contentType;
        this.content = content;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getOriginalFilename() {
        return originalFilename;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    public byte[] getContent() {
        return content;
    }

    @Override
    public boolean isEmpty() {
        if (this.getContentType() == null) {
            return true;
        }
        return this.getContentType().length() == 0;
    }

    @Override
    public long getSize() {
        return this.getContent().length;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return this.getContent();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(this.getContent());
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        FileCopyUtils.copy(this.getContent(), dest);
    }
}

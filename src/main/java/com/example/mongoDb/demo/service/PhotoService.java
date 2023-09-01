package com.example.mongoDb.demo.service;

import com.example.mongoDb.demo.collection.Photo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface PhotoService {
    String addImage(String originalFilename, MultipartFile image) throws IOException;

    Photo getPhoto(String id);
}

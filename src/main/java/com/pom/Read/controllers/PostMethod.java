package com.pom.Read.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.pom.Read.module.FileModel;
import com.pom.Read.service.VersionService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;


@RestController
public class PostMethod {

    @Autowired
    private VersionService versionService;

    @PostMapping("/getFileDetails")
    public FileModel getFileContent(@RequestPart("file") MultipartFile file) {
        if(file.isEmpty())return null;

        return versionService.getFileContent(file);


    }

}

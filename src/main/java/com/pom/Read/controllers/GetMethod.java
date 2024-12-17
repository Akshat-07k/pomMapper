package com.pom.Read.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.pom.Read.service.VersionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class GetMethod {

    @Autowired
    private VersionService versionService;
    
    @GetMapping("/testing")
    public String getMethodName() {
        return "how is it going ";
    }
    
    @GetMapping("/getLattest")
    public String getMethodName(@RequestParam String groupId,@RequestParam String artifactId) {
        return versionService.getLattestVersion(groupId, artifactId);
    }
    

}

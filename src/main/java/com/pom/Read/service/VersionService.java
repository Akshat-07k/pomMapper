package com.pom.Read.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pom.Read.module.Dependency;
import com.pom.Read.module.FileModel;
import com.pom.Read.module.Parent;

import jakarta.servlet.annotation.HttpConstraint;

@Service
public class VersionService {

    public String getLattestVersion(String groupId, String artifactId){
        try{
            String encodedGroupId = URLEncoder.encode(groupId, "UTF-8");
            String encodedArtifactId = URLEncoder.encode(artifactId, "UTF-8");

            String apiUrl = "https://search.maven.org/solrsearch/select?q=g:" + encodedGroupId + "+AND+a:" + encodedArtifactId + "&rows=1&wt=json";

            URL url = new URI(apiUrl).toURL();

            // String connect = 
            HttpURLConnection connect = (HttpURLConnection) url.openConnection();

            connect.setRequestMethod("GET");
            connect.setConnectTimeout(5000);
            connect.setReadTimeout(5000);

            BufferedReader in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while((inputLine = in.readLine()) != null){
                response.append(inputLine);
            }
            in.close();

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response.toString());
            JsonNode docs = rootNode.path("response").path("docs");

            if (docs.isArray() && docs.size() > 0) {
                return docs.get(0).path("latestVersion").asText("N/a");
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return "N/a";
    }

    public FileModel getFileContent(MultipartFile file) {
        if(file.isEmpty()) throw new IllegalArgumentException("File is empty. Please upload a valid file.");

        FileModel fileModel = new FileModel();
        
        try{
            
            InputStream inputStream = file.getInputStream();
            if(inputStream==null)throw new RuntimeException("File input stream is null");
            
            Document document = Jsoup.parse(inputStream,"UTF-8","");//XML to DOM

            Element artifactIdElement = document.selectFirst("project > artifactId");
            fileModel.setArtifactId(artifactIdElement != null ? artifactIdElement.text() : "");

            Element parentElement = document.selectFirst("parent");
            if(parentElement != null){
                Parent parent = new Parent();
                
                Element parElementartifactId = parentElement.selectFirst("artifactId ");
                Element parElementgroupId = parentElement.selectFirst("groupId ");
                Element parElementversion = parentElement.selectFirst("version ");

                parent.setArtifactId(parElementartifactId!=null ?parElementartifactId.text():"");
                parent.setGroupId(parElementgroupId!=null ?parElementgroupId.text():"");
                parent.setVersion(parElementversion!=null ?parElementversion.text():"");

                fileModel.setParent(parent);
                fileModel.setVersion(parElementversion!= null ?parElementversion.text():"");

            }
            else{
                Element versionElement = document.selectFirst("version");
                fileModel.setParent(null);
                fileModel.setVersion(versionElement != null ? versionElement.text() : "");
            }

            //OWN DEPENDENCY 

            Element dependencyManagementElement = document.selectFirst("dependencyManagement");
            if(dependencyManagementElement != null){

                Element dependenciesElement = dependencyManagementElement.selectFirst("dependencies");

                List<Dependency> temp= new ArrayList<>();

                if(dependenciesElement != null)
                for(Element Dependenci : dependenciesElement.select("dependency")){

                    Element groupIdElement = Dependenci.selectFirst("groupId");
                    Element artiIdElement = Dependenci.selectFirst("artifactId");
                    Element versionElement = Dependenci.selectFirst("version");
                    Element scopeElement = Dependenci.selectFirst("scope");

                    Dependency dependency = new Dependency();
                    dependency.setArtifactId(artiIdElement != null ? artiIdElement.text():"");
                    dependency.setGroupId(groupIdElement != null ? groupIdElement.text() : "");
                    dependency.setVersion(versionElement!=null ? versionElement.text():"");
                    dependency.setScope(scopeElement != null ? scopeElement.text(): "");

                    temp.add(dependency);
                }

                fileModel.setDependencies(temp);
            }
            else{
                //empty dependency Element which is given to children 
            }

            // WIll get from parent cause not in management tags 
            Element dependenciesElement = document.selectFirst("dependencies:not(dependencyManagement > dependencies)");
            if(dependenciesElement != null){

                List<Dependency> temp= new ArrayList<>();

                for(Element Dependenci : dependenciesElement.select("dependency")){

                    Element groupIdElement = Dependenci.selectFirst("groupId");
                    Element artiIdElement = Dependenci.selectFirst("artifactId");
                    Element versionElement = Dependenci.selectFirst("version");
                    Element scopeElement = Dependenci.selectFirst("scope");

                    Dependency dependency = new Dependency();
                    dependency.setArtifactId(artiIdElement != null ? artiIdElement.text():"");
                    dependency.setGroupId(groupIdElement != null ? groupIdElement.text() : "");
                    dependency.setVersion(versionElement!=null ? versionElement.text():"");
                    dependency.setScope(scopeElement != null ? scopeElement.text(): "");

                    temp.add(dependency);
                }

                fileModel.setDependenciesFromParent(temp);

            }else{
                //possibly a parent pom.
            }


        }
        catch(Exception e){
            throw new RuntimeException("Error reading file content", e);
        }
        

        return fileModel;
    }
}

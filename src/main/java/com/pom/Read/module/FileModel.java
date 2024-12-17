package com.pom.Read.module;

import java.util.List;

public class FileModel {
    private String artifactId;
    private Parent parent;
    private List<Dependency> dependencies;
    private List<Dependency> dependenciesFromParent;
    private String version;
    
    public FileModel(String artifactId, Parent parent, List<Dependency> dependencies,
            List<Dependency> dependenciesFromParent, String version) {
        this.artifactId = artifactId;
        this.parent = parent;
        this.dependencies = dependencies;
        this.dependenciesFromParent = dependenciesFromParent;
        this.version = version;
    }


    public FileModel() {}
    
    
    public String getArtifactId() {
        return artifactId;
    }
    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }
    public Parent getParent() {
        return parent;
    }
    public void setParent(Parent parent) {
        this.parent = parent;
    }
    public List<Dependency> getDependencies() {
        return dependencies;
    }
    public void setDependencies(List<Dependency> dependencies) {
        this.dependencies = dependencies;
    }
    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }
    public List<Dependency> getDependenciesFromParent() {
        return dependenciesFromParent;
    }
    public void setDependenciesFromParent(List<Dependency> dependenciesFromParent) {
        this.dependenciesFromParent = dependenciesFromParent;
    }

    
}

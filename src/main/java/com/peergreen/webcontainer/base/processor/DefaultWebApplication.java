/**
 * Copyright 2013 Peergreen S.A.S.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.peergreen.webcontainer.base.processor;

import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.Map;

import javax.naming.Context;

import com.peergreen.deployment.Artifact;
import com.peergreen.deployment.facet.archive.Archive;
import com.peergreen.metadata.adapter.AnnotatedClass;
import com.peergreen.webcontainer.WebApplication;

public class DefaultWebApplication implements WebApplication {

    private String contextPath;

    private URI uri;

    private ClassLoader classLoader;
    private ClassLoader parentClassLoader;

    private File unpackedDirectory;

    private final Artifact artifact;

    private Map<String, AnnotatedClass> annotatedClasses;

    private String archiveName;

    private Context javaContext;

    public DefaultWebApplication(Artifact artifact) {
        this.artifact = artifact;
    }


    @Override
    public URI getURI() {
        return uri;
    }

    public void setURI(URI uri) {
        this.uri = uri;
    }


    @Override
    public String getContextPath() {
        return contextPath;
    }

    @Override
    public void setAnnotatedClasses(Map<String, AnnotatedClass> annotatedClasses) {
        this.annotatedClasses = annotatedClasses;
    }

    @Override
    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    @Override
    public List<Archive> getLibraries() {
        return null;
    }

    @Override
    public ClassLoader getClassLoader() {
        return classLoader;
    }

    @Override
    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public File getUnpackedDirectory() {
        return unpackedDirectory;
    }

    @Override
    public void setUnpackedDirectory(File unpackedDirectory) {
        this.unpackedDirectory = unpackedDirectory;
    }


    @Override
    public Artifact getArtifact() {
        return artifact;
    }


    @Override
    public Map<String, AnnotatedClass> getAnnotatedClasses() {
        return annotatedClasses;
    }


    @Override
    public ClassLoader getParentClassLoader() {
        return parentClassLoader;
    }


    @Override
    public void setParentClassLoader(ClassLoader parentClassLoader) {
        this.parentClassLoader = parentClassLoader;
    }


    @Override
    public String getArchiveName() {
        return archiveName;
    }

    public void setArchiveName(String archiveName) {
        this.archiveName = archiveName;
    }


    @Override
    public Context getJavaContext() {
        return javaContext;
    }


    @Override
    public void setJavaContext(Context javaContext) {
        this.javaContext = javaContext;
    }

}

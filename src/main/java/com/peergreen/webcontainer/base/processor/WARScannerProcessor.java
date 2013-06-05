/**
 * Copyright 2012 Peergreen S.A.S.
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
import java.io.IOException;
import java.net.URI;
import java.util.jar.JarFile;

import org.ow2.util.file.FileUtils;
import org.ow2.util.file.FileUtilsException;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;

import com.peergreen.deployment.DiscoveryPhasesLifecycle;
import com.peergreen.deployment.ProcessorContext;
import com.peergreen.deployment.ProcessorException;
import com.peergreen.deployment.facet.archive.Archive;
import com.peergreen.deployment.facet.archive.ArchiveException;
import com.peergreen.deployment.processor.Discovery;
import com.peergreen.deployment.processor.Uri;
import com.peergreen.deployment.processor.Processor;
import com.peergreen.webcontainer.WebApplication;

/**
 * WAR scanner.
 * @author Florent Benoit
 */
@Processor
@Uri(extension = "war")
@Discovery(DiscoveryPhasesLifecycle.FACET_SCANNER)
public class WARScannerProcessor {


    public WARScannerProcessor() {
    }


    /**
     * If the archive is a war file then we will parse the web.xml and flag the archive as being a Web Application
     */
    public void handle(Archive archive, ProcessorContext processorContext) throws ProcessorException {

        DefaultWebApplication webApplication = new DefaultWebApplication(processorContext.getArtifact());

        // Sets the URI of the web application
        URI uri;
        try {
            uri = archive.getURI();
        } catch (ArchiveException e) {
            throw new ProcessorException("Unable to get URI of the archive", e);
        }
        webApplication.setURI(uri);

        // Get context of the given Web application
        String contextPath = uri.getPath();
        if (contextPath.endsWith("/")) {
            contextPath = contextPath.substring(0, contextPath.length() - 1);
        }

        // Keep only the name of the file without any extension
        int lastSlash = contextPath.lastIndexOf("/");
        if (lastSlash != -1) {
            contextPath = contextPath.substring(lastSlash + 1, contextPath.length());
        }
        // remove the .extension
        String fileName = contextPath;
        int dot = contextPath.lastIndexOf(".");
        if (dot != -1) {
            contextPath = contextPath.substring(0, dot);
        }
        contextPath = "/".concat(contextPath);
        webApplication.setContextPath(contextPath);

        // Path to the war file
        File path = new File(uri.getPath());

        // Needs to unpack war if not yet unpacked
        if (path.isFile()) {
            // unpack
            File f = new File(System.getProperty("java.io.tmpdir"), "unpacked");
            File unpacked = new File(f, fileName);
            System.out.println("Unpacking jar to " + unpacked);
            try {
                FileUtils.unpack(new JarFile(uri.getPath()), unpacked);
            } catch (FileUtilsException | IOException e) {
                throw new ProcessorException("Unable to unpack the jar", e);
            }
            webApplication.setUnpackedDirectory(unpacked);
        } else {
            webApplication.setUnpackedDirectory(path);
        }



        // Add facet
        processorContext.addFacet(WebApplication.class, webApplication);
    }





}

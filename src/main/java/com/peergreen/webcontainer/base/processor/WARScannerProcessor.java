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

import java.net.URI;

import com.peergreen.deployment.DiscoveryPhasesLifecycle;
import com.peergreen.deployment.ProcessorContext;
import com.peergreen.deployment.ProcessorException;
import com.peergreen.deployment.facet.archive.Archive;
import com.peergreen.deployment.facet.archive.ArchiveException;
import com.peergreen.deployment.processor.Discovery;
import com.peergreen.deployment.processor.Processor;
import com.peergreen.deployment.processor.Uri;
import com.peergreen.webcontainer.WebApplication;

/**
 * WAR scanner.
 * @author Florent Benoit
 */
@Processor
@Uri(extension = "war")
@Discovery(DiscoveryPhasesLifecycle.FACET_SCANNER)
public class WARScannerProcessor {

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

        webApplication.setArchiveName(contextPath);

        // Keep only the name of the file without any extension
        int lastSlash = contextPath.lastIndexOf("/");
        if (lastSlash != -1) {
            contextPath = contextPath.substring(lastSlash + 1, contextPath.length());
        }
        // remove the .extension
        int dot = contextPath.lastIndexOf(".");
        if (dot != -1) {
            contextPath = contextPath.substring(0, dot);
        }
        contextPath = "/".concat(contextPath);
        webApplication.setContextPath(contextPath);



        // Add facet
        processorContext.addFacet(WebApplication.class, webApplication);
    }





}

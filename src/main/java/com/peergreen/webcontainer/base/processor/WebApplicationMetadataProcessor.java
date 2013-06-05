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

import java.util.Map;

import org.apache.felix.ipojo.annotations.Requires;
import org.ow2.util.archive.api.IArchive;
import org.ow2.util.archive.api.IArchiveManager;
import org.ow2.util.ee.metadata.war.api.IWarMetadata;
import org.ow2.util.ee.metadata.war.api.IWarMetadataFactory;
import org.ow2.util.ee.metadata.war.api.exceptions.WARMetadataException;

import com.peergreen.deployment.Artifact;
import com.peergreen.deployment.ProcessorContext;
import com.peergreen.deployment.ProcessorException;
import com.peergreen.deployment.processor.Phase;
import com.peergreen.deployment.processor.Processor;
import com.peergreen.injection.AnnotatedClass;
import com.peergreen.injection.InjectionService;
import com.peergreen.webcontainer.WebApplication;

/**
 * Analyze metadata of the Web Application
 * @author Florent Benoit
 */
@Processor
@Phase("METADATA")
public class WebApplicationMetadataProcessor {

    private final IWarMetadataFactory warMetadatFactory;

    private final InjectionService injectionService;

    private final IArchiveManager archiveManager;


    public WebApplicationMetadataProcessor(@Requires IWarMetadataFactory warMetadataFactory, @Requires InjectionService injectionService, @Requires IArchiveManager archiveManager) {
        this.warMetadatFactory = warMetadataFactory;
        this.injectionService = injectionService;
        this.archiveManager = archiveManager;
    }

    /**
     * If the archive is a war file then we will parse the web.xml and flag the archive as being a Web Application
     */
    public void handle(WebApplication webApplication, ProcessorContext processorContext) throws ProcessorException {

        Artifact artifact = webApplication.getArtifact();
        IArchive archive = archiveManager.getArchive(webApplication.getUnpackedDirectory());

        // needs to wrap PG archive into OW2 archive
        IWarMetadata warMetadata = null;
        try {
            warMetadata = warMetadatFactory.createArchiveMetadata(archive, webApplication.getClassLoader());
        } catch (WARMetadataException e) {
            throw new ProcessorException("Unable to scan metadata", e);
        }
        processorContext.addFacet(IWarMetadata.class, warMetadata);

        // now gets the injection
        Map<String, AnnotatedClass> map = injectionService.getInjection(artifact, warMetadata);
        webApplication.setAnnotatedClasses(map);

    }




}

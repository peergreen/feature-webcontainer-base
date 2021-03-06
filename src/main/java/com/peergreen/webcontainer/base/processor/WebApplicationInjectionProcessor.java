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
import org.ow2.util.ee.metadata.war.api.IWarMetadata;

import com.peergreen.deployment.Artifact;
import com.peergreen.deployment.ProcessorContext;
import com.peergreen.deployment.ProcessorException;
import com.peergreen.deployment.processor.Phase;
import com.peergreen.deployment.processor.Processor;
import com.peergreen.metadata.adapter.AnnotatedClass;
import com.peergreen.metadata.adapter.MetadataAdapter;
import com.peergreen.webcontainer.WebApplication;

/**
 * Analyze metadata of the Web Application
 * @author Florent Benoit
 */
@Processor
@Phase("INJECTION")
public class WebApplicationInjectionProcessor {

    private final MetadataAdapter metadataAdapter;

    public WebApplicationInjectionProcessor(@Requires MetadataAdapter metadataAdapter) {
        this.metadataAdapter = metadataAdapter;
    }

    /**
     * Perform the injection
     */
    public void handle(WebApplication webApplication, ProcessorContext processorContext) throws ProcessorException {

        Artifact artifact = webApplication.getArtifact();
        IWarMetadata warMetadata = artifact.as(IWarMetadata.class);

        // now gets the injection
        Map<String, AnnotatedClass> map = metadataAdapter.adapt(artifact, warMetadata);
        webApplication.setAnnotatedClasses(map);

    }




}

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

import org.apache.felix.ipojo.annotations.Requires;

import com.peergreen.deployment.ProcessorContext;
import com.peergreen.deployment.ProcessorException;
import com.peergreen.deployment.processor.Phase;
import com.peergreen.deployment.processor.Processor;
import com.peergreen.naming.JavaNamingManager;
import com.peergreen.webcontainer.WebApplication;

/**
 * Perform actions while stopping
 * @author Florent Benoit
 */
@Processor
@Phase("STOP")
public class WebApplicationStopProcessor {

    private final JavaNamingManager javaNamingManager;


    public WebApplicationStopProcessor(@Requires JavaNamingManager javaNamingManager) {
        this.javaNamingManager = javaNamingManager;
    }

    /**
     * Unbind the context for the given classloader
     */
    public void handle(WebApplication webApplication, ProcessorContext processorContext) throws ProcessorException {

        // unbind
        javaNamingManager.unbindClassLoaderContext(webApplication.getClassLoader());
    }




}

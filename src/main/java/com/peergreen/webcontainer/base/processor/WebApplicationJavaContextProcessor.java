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

import javax.naming.Context;
import javax.naming.NamingException;

import org.apache.felix.ipojo.annotations.Requires;

import com.peergreen.deployment.ProcessorContext;
import com.peergreen.deployment.ProcessorException;
import com.peergreen.deployment.processor.Phase;
import com.peergreen.deployment.processor.Processor;
import com.peergreen.naming.JavaContextFactory;
import com.peergreen.naming.JavaNamingManager;
import com.peergreen.webcontainer.WebApplication;

/**
 * Creates java context of the Web Application
 * @author Florent Benoit
 */
@Processor
@Phase("JAVA_CONTEXT")
public class WebApplicationJavaContextProcessor {

    private final JavaContextFactory javaContextFactory;

    private final JavaNamingManager javaNamingManager;


    public WebApplicationJavaContextProcessor(@Requires JavaContextFactory javaContextFactory, @Requires JavaNamingManager javaNamingManager) {
        this.javaContextFactory = javaContextFactory;
        this.javaNamingManager = javaNamingManager;
    }

    /**
     * If the archive is a war file then we will parse the web.xml and flag the archive as being a Web Application
     */
    public void handle(WebApplication webApplication, ProcessorContext processorContext) throws ProcessorException {

        ClassLoader classLoader = webApplication.getClassLoader();

        // build java context
        Context javaContext;
        try {
            javaContext = javaContextFactory.createContext("webapp");
        } catch (NamingException e) {
           throw new ProcessorException("Unable to build context", e);
        }
        webApplication.setJavaContext(javaContext);

        // bind something
        try {
            javaContext.bind("toto", "value1");
        } catch (NamingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        // adds the bind
        //FIXME : remove the binding on stop
        javaNamingManager.bindClassLoaderContext(classLoader, javaContext);
    }




}

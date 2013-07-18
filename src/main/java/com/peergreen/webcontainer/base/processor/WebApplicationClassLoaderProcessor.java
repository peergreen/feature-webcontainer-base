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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import com.peergreen.deployment.ProcessorContext;
import com.peergreen.deployment.ProcessorException;
import com.peergreen.deployment.processor.Phase;
import com.peergreen.deployment.processor.Processor;
import com.peergreen.webcontainer.WebApplication;
import com.peergreen.webcontainer.base.classloader.DynamicImportAllClassLoader;

/**
 * WAR scanner.
 * @author Florent Benoit
 */
@Processor
@Phase("CLASSLOADER")
public class WebApplicationClassLoaderProcessor {


    /**
     * If the archive is a war file then we will parse the web.xml and flag the archive as being a Web Application
     */
    public void handle(WebApplication webApplication, ProcessorContext processorContext) throws ProcessorException {

        // Sets the parent class loader with the OSGi dynamic import classloader if not set
        ClassLoader parentClassLoader = webApplication.getParentClassLoader();
        if (parentClassLoader == null) {
            parentClassLoader = new DynamicImportAllClassLoader();
        }

        // WEB-INF/classes
        File rootWar = webApplication.getUnpackedDirectory();
        File webInfClasses = new File(rootWar + File.separator + "WEB-INF" + File.separator + "classes");
        List<URL> urls = new ArrayList<>();
        try {
            urls.add(rootWar.toURI().toURL());
            urls.add(webInfClasses.toURI().toURL());
        } catch (MalformedURLException e) {
            throw new ProcessorException("Unable to create application classLoader", e);
        }

        // WEB-INF/lib
        File webInfLibDir = new File(rootWar + File.separator + "WEB-INF" + File.separator + "lib");
        File[] libs = webInfLibDir.listFiles();
        for (File lib : libs) {
            try {
                urls.add(lib.toURI().toURL());
            } catch (MalformedURLException e) {
                throw new ProcessorException("Unable to create application classLoader", e);
            }
        }


        // Create a WebApp ClassLoader
        ClassLoader appClassLoader;
            appClassLoader = new URLClassLoader(urls.toArray(new URL[urls.size()]), parentClassLoader);
        webApplication.setClassLoader(appClassLoader);

    }





}

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
import java.io.IOException;
import java.net.URI;
import java.util.jar.JarFile;

import org.ow2.util.file.FileUtils;
import org.ow2.util.file.FileUtilsException;

import com.peergreen.deployment.ProcessorContext;
import com.peergreen.deployment.ProcessorException;
import com.peergreen.deployment.processor.Phase;
import com.peergreen.deployment.processor.Processor;
import com.peergreen.webcontainer.WebApplication;

@Processor
@Phase("UNPACK")
public class WebApplicationUnpackProcessor {

    /**
     * If the archive is a war file then we will parse the web.xml and flag the archive as being a Web Application
     */
    public void handle(WebApplication webApplication, ProcessorContext processorContext) throws ProcessorException {

        URI uri = processorContext.getArtifact().uri();

        // Path to the war file
        File path = new File(uri.getPath());

        // Needs to unpack war if not yet unpacked
        if (path.isFile()) {
            // unpack
            File f = new File(System.getProperty("java.io.tmpdir"), "unpacked");
            File unpacked = new File(f, webApplication.getArchiveName());
            try {
                FileUtils.unpack(new JarFile(uri.getPath()), unpacked);
            } catch (FileUtilsException | IOException e) {
                throw new ProcessorException("Unable to unpack the jar", e);
            }
            webApplication.setUnpackedDirectory(unpacked);
        } else {
            webApplication.setUnpackedDirectory(path);
        }

    }

}

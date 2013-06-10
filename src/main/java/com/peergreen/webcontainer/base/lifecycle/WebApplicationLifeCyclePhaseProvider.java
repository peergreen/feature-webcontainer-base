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
package com.peergreen.webcontainer.base.lifecycle;

import java.util.ArrayList;
import java.util.List;

import com.peergreen.deployment.DeploymentMode;
import com.peergreen.deployment.FacetLifeCyclePhaseProvider;
import com.peergreen.webcontainer.WebApplication;

public class WebApplicationLifeCyclePhaseProvider implements FacetLifeCyclePhaseProvider<WebApplication> {

    private final List<String> deployPhases;
    private final List<String> updatePhases;
    private final List<String> undeployPhases;

    public WebApplicationLifeCyclePhaseProvider() {
        this.deployPhases = new ArrayList<String>();
        deployPhases.add("UNPACK");
        deployPhases.add("CLASSLOADER");
        deployPhases.add("METADATA");
        deployPhases.add("INIT");
        deployPhases.add("START");

        this.undeployPhases = new ArrayList<String>();
        undeployPhases.add("STOP");
        undeployPhases.add("UNDEPLOY");

        this.updatePhases = new ArrayList<String>();
        updatePhases.addAll(undeployPhases);
        updatePhases.addAll(deployPhases);


    }

    @Override
    public List<String> getLifeCyclePhases(DeploymentMode deploymentMode) {
        switch (deploymentMode) {
            case DEPLOY:
                return deployPhases;
            case UPDATE:
                return updatePhases;
            case UNDEPLOY:
                return undeployPhases;
                default : throw new IllegalStateException("Deployment mode '" + deploymentMode + "' not supported");
        }
    }



}

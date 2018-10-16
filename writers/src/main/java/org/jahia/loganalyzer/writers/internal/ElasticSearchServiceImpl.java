package org.jahia.loganalyzer.writers.internal;

/*
 * #%L
 * Jahia Log Analyzer
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2007 - 2016 Jahia
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.jahia.loganalyzer.writers.ElasticSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * A service that either starts an embedded ElasticSearch server or connects to a distant server.
 */
public class ElasticSearchServiceImpl implements ElasticSearchService {

    private static final Logger log =
            LoggerFactory.getLogger(ElasticSearchServiceImpl.class);

    Client client = null;
    BulkProcessor bulkProcessor = null;
    String homePath = new File(".").getPath();

    private String clusterName = "elasticsearch";
    private SortedSet<String> remoteESServers = new TreeSet<String>();

    public ElasticSearchServiceImpl() {
    }

    @Override
    public void setHomePath(String homePath) {
        this.homePath = homePath;
    }

    @Override
    public boolean isRemote() {
        return remoteESServers.size() != 0;
    }

    public void setRemoteServers(String remoteServers) {
        if (remoteServers != null && remoteServers.trim().length() == 0) {
            remoteServers = null;
        }
        if (remoteServers != null) {
            String[] remoteServerArray = remoteServers.split(",");
            for (String remoteServer : remoteServerArray) {
                if (remoteServer.contains(":")) {
                    remoteESServers.add(remoteServer);
                } else {
                    remoteESServers.add(remoteServer + ":9300");
                }
            }
        }
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public synchronized void start() {

        if (client != null) {
            return;
        }

        // this property is used for integration tests, to make sure we don't conflict with an already running ElasticSearch instance.
        if (System.getProperty("org.jahia.loganalyzer.itests.elasticsearch.transport.port") != null) {
            remoteESServers.clear();
            remoteESServers.add("localhost:" + System.getProperty("org.jahia.loganalyzer.itests.elasticsearch.transport.port"));
            log.info("Overriding ElasticSearch address list from system property=" + remoteESServers);
        }
        // this property is used for integration tests, to make sure we don't conflict with an already running ElasticSearch instance.
        if (System.getProperty("org.jahia.loganalyzer.itests.elasticsearch.cluster.name") != null) {
            clusterName = System.getProperty("org.jahia.loganalyzer.itests.elasticsearch.cluster.name");
            log.info("Overriding cluster name from system property=" + clusterName);
        }

        Settings settings = Settings.builder()
                .put("cluster.name", clusterName).build();
        if (remoteESServers.size() != 0) {
            log.info("Connecting to remote ElasticSearch at addresses: " + remoteESServers + "...");
            TransportClient transportClient = new PreBuiltTransportClient(settings, Collections.<Class<? extends Plugin>>emptySet());
            for (String remoteESServer : remoteESServers) {
                String[] remoteESServerParts = remoteESServer.split(":");
                try {
                    transportClient.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(remoteESServerParts[0]), Integer.parseInt(remoteESServerParts[1])));
                } catch (UnknownHostException uhe) {
                    log.error("Error resolving node name " + remoteESServerParts[0], uhe);
                }
            }
            client = transportClient;
        } else {
            log.error("You have to setup and configure a remote ES 5 server as an embedded ES servers is no longer available");
        }

        if (client != null && bulkProcessor == null) {
            bulkProcessor = BulkProcessor.builder(
                    client,
                    new BulkProcessor.Listener() {
                        @Override
                        public void beforeBulk(long executionId,
                                               BulkRequest request) {
                            log.debug("Before Bulk");
                        }

                        @Override
                        public void afterBulk(long executionId,
                                              BulkRequest request,
                                              BulkResponse response) {
                            log.debug("After Bulk");
                        }

                        @Override
                        public void afterBulk(long executionId,
                                              BulkRequest request,
                                              Throwable failure) {
                            log.error("After Bulk (failure)", failure);
                        }
                    })
                    .build();
        }
    }

    @Override
    public Client getClient() {
        if (client == null) {
            start();
        }
        return client;
    }

    @Override
    public BulkProcessor getBulkProcessor() {
        if (bulkProcessor == null) {
            start();
        }
        return bulkProcessor;
    }

    public void stop() {
        if (bulkProcessor != null) {
            bulkProcessor.close();
            bulkProcessor = null;
        }
        if (client != null) {
            client.close();
            client = null;
        }
    }

}

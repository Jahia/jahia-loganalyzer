package org.jahia.loganalyzer.writers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.jahia.loganalyzer.ResourceUtils;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * A service that either starts an embedded ElasticSearch server or connects to a distant server.
 */
public class ElasticSearchService {

    private static final Log log =
            LogFactory.getLog(ElasticSearchService.class);

    private static final ElasticSearchService instance = new ElasticSearchService();
    Node node = null;
    Client client = null;
    BulkProcessor bulkProcessor = null;
    String homePath = new File(".").getPath();

    private SortedSet<String> remoteESServers = new TreeSet<String>();

    public ElasticSearchService() {
    }

    public static ElasticSearchService getInstance() {
        return instance;
    }

    public void setHomePath(String homePath) {
        this.homePath = homePath;
    }

    public boolean isRemote() {
        return remoteESServers.size() != 0;
    }

    public synchronized void start() {

        if (client != null) {
            return;
        }
        if (System.getProperty("remoteServers") != null) {
            String[] remoteServerArray = System.getProperty("remoteServers").split(",");
            for (String remoteServer : remoteServerArray) {
                if (remoteServer.contains(":")) {
                    remoteESServers.add(remoteServer);
                } else {
                    remoteESServers.add(remoteServer + ":9300");
                }
            }
        }
        if (remoteESServers.size() != 0) {
            TransportClient transportClient = TransportClient.builder().build();
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
            System.setProperty("es.path.home", homePath);
            File esSitePluginDirectory = new File(homePath, "plugins/loganalyzer");
            ResourceUtils.extractZipResource("loganalyzer-es-site-plugin.jar", esSitePluginDirectory);
            node = NodeBuilder.nodeBuilder().node();
            client = node.client();
        }

        if (client != null && bulkProcessor == null) {
            bulkProcessor = BulkProcessor.builder(
                    client,
                    new BulkProcessor.Listener() {
                        @Override
                        public void beforeBulk(long executionId,
                                               BulkRequest request) {
                        }

                        @Override
                        public void afterBulk(long executionId,
                                              BulkRequest request,
                                              BulkResponse response) {
                        }

                        @Override
                        public void afterBulk(long executionId,
                                              BulkRequest request,
                                              Throwable failure) {
                        }
                    })
                    .build();
        }
    }

    public Client getClient() {
        if (client == null) {
            start();
        }
        return client;
    }

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
        if (remoteESServers.size() == 0) {
            if (node != null) {
                node.close();
                node = null;
            }
        }
    }

}

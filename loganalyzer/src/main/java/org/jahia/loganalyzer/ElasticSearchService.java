package org.jahia.loganalyzer;

import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;

import java.io.File;

/**
 * Created by loom on 11.03.16.
 */
public class ElasticSearchService {

    private static final ElasticSearchService instance = new ElasticSearchService();
    Node node = null;
    Client client = null;
    BulkProcessor bulkProcessor = null;
    String homePath = new File(".").getPath();

    public ElasticSearchService() {
    }

    public static ElasticSearchService getInstance() {
        return instance;
    }

    public void setHomePath(String homePath) {
        this.homePath = homePath;
    }

    public synchronized void start() {
        System.setProperty("es.path.home", homePath);
        File esSitePluginDirectory = new File(homePath, "plugins/loganalyzer");
        ResourceUtils.extractZipResource("loganalyzer-es-site-plugin.jar", esSitePluginDirectory);

        if (node == null && client == null && bulkProcessor == null) {
            node = NodeBuilder.nodeBuilder().node();
            client = node.client();
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
        if (node != null) {
            if (bulkProcessor != null) {
                bulkProcessor.close();
            }
            client = null;
            bulkProcessor = null;
            node.close();
            node = null;
        }
    }

}

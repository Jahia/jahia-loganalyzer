package org.jahia.loganalyzer;

import org.elasticsearch.client.Client;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;

import java.io.File;

/**
 * Created by loom on 11.03.16.
 */
public class ElasticSearchService {

    private static final ElasticSearchService instance = new ElasticSearchService();
    Node node;
    Client client;
    String homePath = new File(".").getPath();

    public ElasticSearchService() {
    }

    public static ElasticSearchService getInstance() {
        return instance;
    }

    public void setHomePath(String homePath) {
        this.homePath = homePath;
    }

    public void start() {
        System.setProperty("es.path.home", homePath);
        if (node == null && client == null) {
            node = NodeBuilder.nodeBuilder().node();
            client = node.client();
        }
    }

    public Client getClient() {
        if (client == null) {
            start();
        }
        return client;
    }

    public void stop() {
        if (node != null) {
            client = null;
            node.close();
            node = null;
        }
    }

}

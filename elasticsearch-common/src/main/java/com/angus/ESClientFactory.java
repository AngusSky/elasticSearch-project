package com.angus;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;

public class ESClientFactory {

    private HttpHost[] httpHosts;
    private RestClientBuilder builder;

    private RestHighLevelClient restHighLevelClient;

    public void init() {
        builder = RestClient.builder(this.httpHosts).setMaxRetryTimeoutMillis(60000);

        restHighLevelClient = new RestHighLevelClient(builder);
    }

    public HttpHost[] getHttpHosts() {
        return httpHosts;
    }

    public void setHttpHosts(HttpHost[] httpHosts) {
        this.httpHosts = httpHosts;
    }

    public RestHighLevelClient getRestHighLevelClient() {
        return restHighLevelClient;
    }

    public void setRestHighLevelClient(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
    }
}

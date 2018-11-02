package com.angus;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.*;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class ESTemplate {
    private static final Logger logger = LoggerFactory.getLogger(ESTemplate.class);

    private ESClientFactory esClientFactory;
    private RestHighLevelClient restHighLevelClient;

    public void init() {
        this.restHighLevelClient = esClientFactory.getRestHighLevelClient();
    }

    public ESClientFactory getEsClientFactory() {
        return esClientFactory;
    }

    public void setEsClientFactory(ESClientFactory esClientFactory) {
        this.esClientFactory = esClientFactory;
    }

    public void close() {
        if (restHighLevelClient != null) {
            try {
                restHighLevelClient.close();
            } catch (IOException e) {
                logger.error("clost restHighLevelClient error", e);
            }
        }
    }

    /**
     * 单条插入
     *
     * @param index 索引
     * @param type  type名称
     * @param obj   插入对象
     * @return
     */
    public IndexResponse indexSingleDoc(String index, String type, ESGenericBasic obj) throws Exception {
        IndexResponse indexResponse = null;

        IndexRequest indexRequest = new IndexRequest(index, type);
        indexRequest.source(JSONObject.toJSONString(obj), XContentType.JSON).id(String.valueOf(obj.getId()));
        String routing = obj.getRouting();
        if (StringUtils.isNotBlank(routing)) {
            indexRequest.routing(routing);
        }

        indexResponse = restHighLevelClient.index(indexRequest);

        return indexResponse;
    }

    /**
     * 批量插入
     *
     * @param index 索引
     * @param type  type名称
     * @param objs  插入对象
     * @return
     */
    public BulkResponse indexBulk(String index, String type, List<ESGenericBasic> objs) throws Exception {
        BulkResponse bulkResponse = null;

        if (!CollectionUtils.isEmpty(objs)) {
            if(isExistIndex(index)){
                BulkRequest bulkRequest = new BulkRequest();
                for (int i = 0; i < objs.size(); i++) {
                    IndexRequest indexRequest = new IndexRequest(index, type);
                    String routing = objs.get(i).getRouting();
                    if (StringUtils.isNotBlank(routing)) {
                        indexRequest.routing(routing);
                    }
                    indexRequest.source(JSONObject.toJSONString(objs.get(i)), XContentType.JSON).id(objs.get(i).getId());
                    bulkRequest.add(indexRequest);
                }
                bulkResponse = restHighLevelClient.bulk(bulkRequest);
            }
        }

        return bulkResponse;
    }

    /**
     * 根据id删除
     *
     * @param index 索引
     * @param type  type名称
     * @param obj   删除对象
     * @return
     */
    public DeleteResponse deleteDoc(String index, String type, ESGenericBasic obj) throws Exception {
        DeleteResponse deleteResponse = null;

        DeleteRequest deleteRequest = new DeleteRequest(index, type, obj.getId());
        String routing = obj.getRouting();
        if (StringUtils.isNotBlank(routing)) {
            deleteRequest.routing(obj.getRouting());
        }

        restHighLevelClient.delete(deleteRequest);


        return deleteResponse;
    }

    /**
     * 查询
     *
     * @param searchRequest
     * @return
     */
    public SearchResponse search(SearchRequest searchRequest) throws Exception {

        return restHighLevelClient.search(searchRequest);
    }

    /**
     * scroll查询
     *
     * @param scrollRequest
     * @return
     */
    public SearchResponse search(SearchScrollRequest scrollRequest) {
        SearchResponse searchResponse = null;
        try {
            searchResponse = restHighLevelClient.searchScroll(scrollRequest);
        } catch (IOException e) {
            logger.error("search error", e);
        }

        return searchResponse;
    }

    /**
     * 查询数量
     *
     * @param searchRequest
     * @return
     */
    public Long count(SearchRequest searchRequest) throws Exception {
        Long count = 0L;

        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
        if (searchResponse != null) {
            count = searchResponse.getHits().totalHits;
        }

        return count;
    }

    /**
     * 校验index是否存在
     *
     * @param indices
     * @return
     */
    public boolean isExistIndex(String... indices) {
        GetIndexRequest getIndexRequest = new GetIndexRequest();
        getIndexRequest.indices(indices);
        getIndexRequest.local(false);
        getIndexRequest.humanReadable(true);
        getIndexRequest.includeDefaults(false);
        getIndexRequest.indicesOptions(IndicesOptions.strictExpandOpen());
        try {
            return restHighLevelClient.indices().exists(getIndexRequest);
        } catch (IOException e) {
            logger.error("isExistIndex error", e);
        }

        return false;
    }

    /**
     * 滚动完成，清除滚动上下文
     *
     * @param scrollId
     * @return
     * @throws IOException
     */
    public boolean clearScroll(String scrollId) throws Exception {
        ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
        clearScrollRequest.addScrollId(scrollId);
        ClearScrollResponse clearScrollResponse = restHighLevelClient.clearScroll(clearScrollRequest);
        return clearScrollResponse.isSucceeded();
    }
}

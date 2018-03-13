package com.suqc.dao;

import com.suqc.pojo.SearchItem;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SearchItemDaoImpl implements SolrDao<SearchItem>{

    @Autowired
    private HttpSolrServer httpSolrServer;

    public SearchItem getById(Long id) throws Exception{
        //创建查询条件
        SolrQuery query = new SolrQuery();
        query.setQuery("id:" + id);
        //查询并返回结果
        QueryResponse queryResponse = this.httpSolrServer.query(query);
        List<SearchItem> list =  queryResponse.getBeans(SearchItem.class);
        return list.get(0);
    }

    public void update(SearchItem searchItem) {

    }

    public void deleteById(Long id) {

    }

    public void save(SearchItem searchItem) {

    }
}

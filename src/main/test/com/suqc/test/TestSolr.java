package com.suqc.test;

import com.suqc.dao.SearchItemDaoImpl;
import com.suqc.dao.SolrDao;
import com.suqc.pojo.SearchItem;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrResponse;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.FacetParams;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class TestSolr {
    HttpSolrServer server = null;
    @Before
    public void test0(){
        ApplicationContext ac = new ClassPathXmlApplicationContext("spring-core.xml" );
        server = ac.getBean("httpSolrServer",HttpSolrServer.class);
    }
    @Test
    public void test3() throws Exception{
        SolrQuery query = new SolrQuery("*:*");
        query.setIncludeScore(false);//fl是否包含score域
        query.setFacet(true);//开启facet功能
        query.addFacetField("item_category_name","item_price");//两个域有各自独立的结果
//        query.addFacetQuery("");
        //FacetComponet有两种排序选择,分别是count和index
        //count是按每个词出现的次数,index是按词的字典顺序.如果不指定facet.sort,solr默认是按count排序
        //query.setFacetSort("count");
        query.setFacetSort(FacetParams.FACET_SORT_COUNT);//同上
        query.setFacetLimit(10);//设置返回结果条数,-1为返回所有,默认值为100
        query.setFacetMinCount(1);//设置count的最小返回值,默认为0
        query.setFacetMissing(false);//不统计null的值
//        query .setFacetPrefix();//设置前缀
        System.out.println(query.toString());
        QueryResponse response = server.query(query);
        printfFacet(response);
    }
    public void printfFacet(QueryResponse response){
        System.out.println("--------单个facet结果---------");
        List<FacetField.Count> categoryNameFacetList = response.getFacetField("item_category_name").getValues();//获取单个facet结果
        for(FacetField.Count c:categoryNameFacetList){
            System.out.println(c.getName()+"#"+c.getCount());
        }
        System.out.println("--------多个facet结果---------");
        List<FacetField> facetFields =response.getFacetFields();
        for(FacetField f:facetFields){
            List<FacetField.Count> counts = f.getValues();
            for(FacetField.Count c:counts){
                System.out.println(c.getName()+"@"+c.getCount());
            }
        }
    }
    @Test
    public void test2()throws Exception{
        SolrQuery query = new SolrQuery();
        query.setQuery("*:*");
        QueryResponse response = server.query(query);
        //SolrDocumentList list = response.getResults();
        List<SearchItem> list = response.getBeans(SearchItem.class);
        for(SearchItem si:list){
            System.out.println(si);//有问题   全是空   ?????
        }
    }
    @Test
    public void test1() throws Exception {
        SolrQuery query = new SolrQuery();
        //q
        query.setQuery("item_title:华为");
        //fq
        query.setFilterQueries("item_category_name:手机");//多个会覆盖
        //query.addFilterQuery("","");//多过滤条件
        //高亮
        query.setHighlight(true);
        query.addHighlightField("item_title");
        query.setHighlightSimplePre("<em>");
        query.setHighlightSimplePost("</em>");
        //start rows
        query.setStart(0);
        query.setRows(10);
        //sort
        query.setSort("item_price", SolrQuery.ORDER.desc);//会覆盖
        //query.addSort("","");//会在原有基础上添加  多条件排序
        //fl
        query.setParam("fl","id,item_title");
        QueryResponse response = server.query(query);
        System.out.println("找到记录数为:"+response.getResults().getNumFound()+"条");
        printfDoc(response);
        System.out.println("------------------------highting---------------------------");
        printfHight(response.getHighlighting());
        server.commit();
    }

    private void printfDoc(QueryResponse response){
        SolrDocumentList docList = response.getResults();
        for(SolrDocument doc:docList){
            Collection<String> names = doc.getFieldNames();
            for(String name:names){
                System.out.println("("+name+")"+"--->"+doc.getFieldValue(name));
            }
            System.out.println(response.getHighlighting().get(doc.get("id")).get("item_title"));
            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@");
        }
    }

    private void printfHight(Map<String, Map<String, List<String>>> highlighting) {
        for(String id:highlighting.keySet()){
            Map<String,List<String>> value = highlighting.get(id);
            System.out.print(id+"--->");
            for(String key:value.keySet()){
                System.out.println(key+"/"+value.get(key));
            }
        }
    }
    @After
    public void testEnd(){
        server.shutdown();
    }
}

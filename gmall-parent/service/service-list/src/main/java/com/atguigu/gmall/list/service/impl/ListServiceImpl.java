package com.atguigu.gmall.list.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.list.service.ListService;
import com.atguigu.gmall.product.client.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListServiceImpl implements ListService {

    @Autowired
    private ProductFeignClient productFeignClient;
    @Autowired
    ElasticsearchRestTemplate elasticsearchRestTemplate;
    @Autowired
    GoodsElasticsearchRepository goodsElasticsearchRepository;
    @Autowired
    RestHighLevelClient restHighLevelClient;


    @Override
    public List<JSONObject> getBaseCategoryList() {

     List<JSONObject> list=productFeignClient.getBaseCategoryList();

     return list;

    }

    @Override
    public void createGoodsIndex() {
        elasticsearchRestTemplate.createIndex(Goods.class);
        elasticsearchRestTemplate.putMapping(Goods.class);
    }

    @Override
    public void onSale(long skuId) {
        //存放的对象
        Goods goods = new Goods();
        //拿基础数据
        SkuInfo skuInfo = productFeignClient.getSkuInfoById(skuId);
        //拿属性数据
       List<SearchAttr> searchAttrs = productFeignClient.getSearchAttrList(skuId);
        //商标数据
        BaseTrademark trademark = productFeignClient.getTrademarkById(skuInfo.getTmId());
        //拿到类目及各种信息
        BaseCategoryView view = productFeignClient.getCategoryViewByCategory3Id(skuInfo.getCategory3Id());

        //
        goods.setCategory1Id(view.getCategory1Id());
        goods.setCategory1Name(view.getCategory1Name());
        goods.setCategory2Id(view.getCategory2Id());
        goods.setCategory2Name(view.getCategory2Name());
        goods.setCategory3Name(view.getCategory3Name());
        goods.setHotScore(0l);
        goods.setAttrs(searchAttrs);
        goods.setCategory3Id(skuInfo.getCategory3Id());
        goods.setTitle(skuInfo.getSkuName());
        goods.setCreateTime(new Date());
        goods.setTmId(skuInfo.getTmId());
        goods.setId(skuId);
        goods.setDefaultImg(skuInfo.getSkuDefaultImg());
        goods.setTmName(trademark.getTmName());
        goods.setTmLogoUrl(trademark.getLogoUrl());
        goods.setPrice(skuInfo.getPrice().doubleValue());

        goodsElasticsearchRepository.save(goods);
    }

    @Override
    public void cancelSale(long skuId) {
        goodsElasticsearchRepository.deleteById(skuId);
    }

    @Override
    public SearchResponseVo list(SearchParam searchParam) {
        SearchResponse searchResponse = null;
        //封装查询条件
        SearchRequest searchRequest = getSearchRequest(searchParam);
        //查询
        try {
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //处理查询结果
        SearchResponseVo searchResponseVo = getSearchResponseVo(searchResponse);
        // 返回去
        return searchResponseVo;
    }

    private SearchResponseVo getSearchResponseVo(SearchResponse searchResponse) {
        SearchResponseVo searchResponseVo = new SearchResponseVo();
        //解析返回去的东西
        SearchHits hits = searchResponse.getHits();
        SearchHit[] hits1 = hits.getHits();
        List<Goods> list = new ArrayList();
        if (hits1 != null && hits1.length>0){
            //拿到全部符合条件的数据
            for (SearchHit document : hits1) {
                String json = document.getSourceAsString();
                Goods goods = JSON.parseObject(json, Goods.class);
                Map<String, HighlightField> highlightFields = document.getHighlightFields();

               if (null != highlightFields){
                   HighlightField title = highlightFields.get("title");
                   if (title != null){
                       Text text = title.getFragments()[0];
                       String realTitle = text.string();
                       goods.setTitle(realTitle);
                   }
               }

                list.add(goods);
            }
            searchResponseVo.setGoodsList(list);

            //处理聚合
            ParsedLongTerms tmIdAgg = searchResponse.getAggregations().get("tmIdAgg");
            List<SearchResponseTmVo> collect = tmIdAgg.getBuckets().stream().map(tmIdAggBucket -> {
                SearchResponseTmVo searchResponseTmVo = new SearchResponseTmVo();
                long tmIdKey = tmIdAggBucket.getKeyAsNumber().longValue();
                ParsedStringTerms tmNameAgg = tmIdAggBucket.getAggregations().get("tmNameAgg");
                List<? extends Terms.Bucket> tmNameAggBuckets = tmNameAgg.getBuckets();
                String tmNameKey = tmNameAggBuckets.get(0).getKeyAsString();

                ParsedStringTerms tmUrlAgg =  tmIdAggBucket.getAggregations().get("tmLogoUrlAgg");
                List<? extends Terms.Bucket> tmUrlAggBuckets = tmUrlAgg.getBuckets();
                String tmLogoUrlKey = tmUrlAggBuckets.get(0).getKeyAsString();
                searchResponseTmVo.setTmId(tmIdKey);
                searchResponseTmVo.setTmLogoUrl(tmLogoUrlKey);
                searchResponseTmVo.setTmName(tmNameKey);
                return searchResponseTmVo;
            }).collect(Collectors.toList());
          searchResponseVo.setTrademarkList(collect);
            //属性解析
            ParsedNested attrsAgg = searchResponse.getAggregations().get("attrsAgg");
            ParsedLongTerms attrIdAgg = attrsAgg.getAggregations().get("attrIdAgg");

          List<SearchResponseAttrVo> searchResponseAttrVos = attrIdAgg.getBuckets().stream().map(attrIdBucket->{
               SearchResponseAttrVo searchResponseAttrVo = new SearchResponseAttrVo();
               long attrIdKey = ((Terms.Bucket) attrIdBucket).getKeyAsNumber().longValue();
               searchResponseAttrVo.setAttrId(attrIdKey);
               ParsedStringTerms attrNameAgg = ((Terms.Bucket) attrIdBucket).getAggregations().get("attrNameAgg");
               String attrNameKey = attrNameAgg.getBuckets().get(0).getKeyAsString();
               searchResponseAttrVo.setAttrName(attrNameKey);
             ParsedStringTerms attrValueAgg =  ((Terms.Bucket) attrIdBucket).getAggregations().get("attrValueAgg");
             List<String> attrValueList=attrValueAgg.getBuckets().stream().map(attrValueBucker->{
                 String attrValueKey = ((Terms.Bucket) attrValueBucker).getKeyAsString();
                return attrValueKey;
             }).collect(Collectors.toList());
               searchResponseAttrVo.setAttrValueList(attrValueList);
               return searchResponseAttrVo;
           }).collect(Collectors.toList());
          searchResponseVo.setAttrsList(searchResponseAttrVos);
        }
        return searchResponseVo;
    }

    private SearchRequest getSearchRequest(SearchParam searchParam) {
        SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //先设置index和type
        searchRequest.indices("goods");
        searchRequest.types("info");
        Long category3Id = searchParam.getCategory3Id();
        String keyword = searchParam.getKeyword();
        String[] props = searchParam.getProps();
        String trademark = searchParam.getTrademark();

        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        if (null != props && props.length >0){
            for (String prop : props) {
                String[] split = prop.split(":");
                long attrId = Long.parseLong(split[0]);
                String attrValueName = split[1];
                String attrName =  split[2];
                BoolQueryBuilder boolQueryBuilderNested = new BoolQueryBuilder();
                TermQueryBuilder termQueryBuilderAttrId = new TermQueryBuilder("attrs.attrId", attrId);
                boolQueryBuilderNested.filter(termQueryBuilderAttrId);
                MatchQueryBuilder matchQueryBuilderAttrValueName= new MatchQueryBuilder("attrs.attrValue", attrValueName);
                boolQueryBuilderNested.must(matchQueryBuilderAttrValueName);
                NestedQueryBuilder nestedQueryBuilder = new NestedQueryBuilder("attrs", boolQueryBuilderNested, ScoreMode.None);



                boolQueryBuilder.filter(nestedQueryBuilder);
            }

        }
        //关键字
        if (!StringUtils.isEmpty(keyword)){
            MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("title",keyword);
            boolQueryBuilder.must(matchQueryBuilder);

        }
    if (!StringUtils.isEmpty(trademark) ){
        TermQueryBuilder termQueryBuilder = new TermQueryBuilder("tmId",trademark.split(":")[0]);
        boolQueryBuilder.filter(termQueryBuilder);
    }
        //封装条件
        if (category3Id != null && category3Id > 0){
            TermQueryBuilder termQueryBuilder = new TermQueryBuilder("category3Id", category3Id);
            boolQueryBuilder.filter(termQueryBuilder);
        }
        searchSourceBuilder.query(boolQueryBuilder);
        //聚合商标
        TermsAggregationBuilder  termsAggregationBuilder= AggregationBuilders.terms("tmIdAgg").field("tmId")
                .subAggregation(AggregationBuilders.terms("tmNameAgg").field("tmName"))
                .subAggregation(AggregationBuilders.terms("tmLogoUrlAgg").field("tmLogoUrl"));
        searchSourceBuilder.aggregation(termsAggregationBuilder);

        //属性聚合
        NestedAggregationBuilder nestedAggregationBuilder = AggregationBuilders.nested("attrsAgg", "attrs")
                .subAggregation(AggregationBuilders.terms("attrIdAgg").field("attrs.attrId")
                        .subAggregation(AggregationBuilders.terms("attrNameAgg").field("attrs.attrName"))
                        .subAggregation(AggregationBuilders.terms("attrValueAgg").field("attrs.attrValue")));
        searchSourceBuilder.aggregation(nestedAggregationBuilder);

        searchSourceBuilder.size(20);
        searchSourceBuilder.from(0);
        //高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<span style='color:red; font-weight:bolder'>");
        highlightBuilder.field("title");
        highlightBuilder.postTags("</span>");

        searchSourceBuilder.highlighter(highlightBuilder);


        searchRequest.source(searchSourceBuilder);
        System.out.println(searchSourceBuilder.toString());
        return searchRequest;
    }
}

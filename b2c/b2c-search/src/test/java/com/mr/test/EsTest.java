package com.mr.test;

import com.mr.SearchApplication;
import com.mr.client.GoodsClient;
import com.mr.dao.GoodsRepository;
import com.mr.pojo.Goods;
import com.mr.service.GoodsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SearchApplication.class)
public class EsTest {

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private GoodsRepository goodsRepository;
    @Test
    public void createIndex(){
        //创建 商品的索引与映射
        elasticsearchTemplate.createIndex(Goods.class);
        elasticsearchTemplate.putMapping(Goods.class);
    }
    @Test
    public void saveGoodForItem(){
       Goods goods = goodsService.getGood(2L);
        goodsRepository.save(goods);
    }
    @Test
    public void saveGoodListForItem(){

        ArrayList l =new ArrayList();
        //查询数据(一次全部查询，适用于少量数据)
        goodsClient.querySpuByPage(0,300,null,null).getItems().forEach(spuBo -> {
            Goods goods = goodsService.getGood(spuBo.getId());
                l.add(goods);
            });
        goodsRepository.saveAll(l);
    }
}
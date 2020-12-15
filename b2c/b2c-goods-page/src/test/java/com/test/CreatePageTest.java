package com.test;

import com.item.common.utils.PageResult;
import com.mr.GoodPageApplication;
import com.mr.client.GoodsClient;
import com.mr.pojo.SpuBo;
import com.mr.service.FileStaticService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { GoodPageApplication.class})
public class CreatePageTest {

    @Autowired
   private GoodsClient goodsClient;

    @Autowired
    private FileStaticService fileService;

    @Test
    public void create() {
        //查询spu分页数据
        PageResult<SpuBo> spuPage=goodsClient.querySpuByPage(0,200,null,null);
        spuPage.getItems().forEach(spuBo -> {
            try {
                //循环创建静态文件
                fileService.createStaticHtml(spuBo.getId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

}

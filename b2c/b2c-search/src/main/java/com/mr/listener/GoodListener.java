package com.mr.listener;

import com.item.common.rebbitmq.MqMessageConstant;
import com.mr.service.GoodsService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GoodListener {

    @Autowired
    private GoodsService goodsIndexService;
    //新增/修改spu 修改es库
    // 创建队列，绑定交换机 指定类型，持久化routing等参数
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqMessageConstant.SPU_QUEUE_SEARCH_SAVE, durable = "true"),
            exchange = @Exchange(
                    value =   MqMessageConstant.SPU_EXCHANGE_NAME,
                    ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC
            ),
            key = {MqMessageConstant.SPU_ROUT_KEY_SAVE,MqMessageConstant.SPU_ROUT_KEY_UPDATE}))
    public void saveEs(String id){
        try {
            System.out.println("es新增/修改spu数据：" + id);
            goodsIndexService.saveGoodForEs(Long.valueOf(id));
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //删除es数据
    // 创建队列，绑定交换机 指定类型，持久化routing等参数
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqMessageConstant.SPU_QUEUE_SEARCH_DELETE, durable = "true"),
            exchange = @Exchange(
                    value =   MqMessageConstant.SPU_EXCHANGE_NAME,
                    ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC
            ),
            key = {MqMessageConstant.SPU_ROUT_KEY_DELETE}))
    public void deleteEs(String id){
        try {
            System.out.println("es需要删除：" + id);
            goodsIndexService.deleteGoodForEs(Long.valueOf(id));
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
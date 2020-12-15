package com.mr.client;

import com.mr.api.GoodsApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "item-service")
public interface GoodsClient  extends GoodsApi {


}
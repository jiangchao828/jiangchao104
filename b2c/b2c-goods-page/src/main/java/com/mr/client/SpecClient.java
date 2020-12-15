package com.mr.client;

import com.mr.api.SpecApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("item-service")
public interface SpecClient extends SpecApi {

}
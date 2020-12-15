package com.mr.client;

import com.mr.api.BrandApi;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(value = "item-service")
public interface BrandClient extends BrandApi {


}

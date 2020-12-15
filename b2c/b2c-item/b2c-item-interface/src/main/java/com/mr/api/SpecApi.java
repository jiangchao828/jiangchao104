package com.mr.api;

import com.mr.pojo.SpecGroup;
import com.mr.pojo.SpecParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


import java.util.List;

@RequestMapping("spec")
public interface SpecApi {

    @GetMapping("/groups/{cid}")
    public List<SpecGroup> querySpecGroupList(
            @PathVariable("cid") Long cid);

    /**
     * 根据规格组id查询规格参数
     * @param gid
     * @return
     */
    @GetMapping("/params")
    public List<SpecParam> querySpecParam(
            @RequestParam(value="gid", required = false) Long gid,
            @RequestParam(value="cid", required = false) Long cid,
            @RequestParam(value="searching", required = false) Boolean searching,
            @RequestParam(value="generic", required = false) Boolean generic
    );
}

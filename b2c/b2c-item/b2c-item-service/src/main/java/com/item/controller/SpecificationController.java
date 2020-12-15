package com.item.controller;

import com.item.service.SpecificationService;
import com.item.service.SpecGroupService;
import com.mr.pojo.SpecGroup;
import com.mr.pojo.SpecParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("spec")
public class SpecificationController {
    @Autowired
    private SpecificationService specificationService;

    @Autowired
    private  SpecGroupService specGroupService;

    @GetMapping("groups/{cid}")
    public ResponseEntity<List<SpecGroup>> querySpecGroups(@PathVariable("cid") Long cid){
        List<SpecGroup> list = this.specificationService.querySpecGroups(cid);
        if(list == null || list.size() == 0){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(list);
    }
    // 增加
    @PostMapping(value="group")
    public ResponseEntity saveSpecGroup(@RequestBody SpecGroup specGroup){

        return specificationService.saveOrUpdateSpecGroup(specGroup);
    }
    // 修改
    @PutMapping(value="group")
    public ResponseEntity updateSpecGroup(@RequestBody SpecGroup specGroup){

        return specificationService.saveOrUpdateSpecGroup(specGroup);
    }

    // 删除
    @DeleteMapping
    public ResponseEntity delete(@RequestParam("id") Long id){
        return this.specificationService.findById(id);
    }



//参数表
    //查询
    @GetMapping("/params")
    public ResponseEntity<List<SpecParam>> querySpecParam(@RequestParam(value="gid", required = false) Long gid,
                                                          @RequestParam(value="cid", required = false) Long cid,
                                                          @RequestParam(value="searching", required = false)Boolean searching,
                                                          @RequestParam(value="generic", required = false) Boolean generic){
        List<SpecParam> list = this.specGroupService.querySpecParams(gid,cid,searching,generic);
        if(list == null || list.size() == 0){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(list);
    }
    // 增加
    @PostMapping(value="param")
    public ResponseEntity addSpecParam(@RequestBody SpecParam specParam){

        return specGroupService.saveOrUpdateSpecParam(specParam);
    }
    // 修改
    @PutMapping(value="param")
    public ResponseEntity editSpecParam(@RequestBody SpecParam specParam){

        return specGroupService.saveOrUpdateSpecParam(specParam);
    }

    // 删除
    @DeleteMapping(value="param/{id}")
    public ResponseEntity remove(@PathVariable("id") Long id){
        return this.specGroupService.findById(id);
    }




}

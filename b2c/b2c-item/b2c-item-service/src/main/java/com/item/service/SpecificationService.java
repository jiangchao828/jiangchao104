package com.item.service;

import com.item.mapper.SpecGroupMapper;

import com.mr.pojo.SpecGroup;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;


import javax.annotation.Resource;
import java.util.List;

@Service
public class SpecificationService {
    @Resource
    private SpecGroupMapper specGroupMapper;



    public List<SpecGroup> querySpecGroups(Long cid) {
        Example example=new Example(SpecGroup.class);
        example.createCriteria().andEqualTo("cid",cid);
        return specGroupMapper.selectByExample(example);
    }

    //增加
    public ResponseEntity saveOrUpdateSpecGroup(SpecGroup specGroup) {
        if (specGroup != null) {
            if (specGroup.getId() != null && specGroup.getId() != 0) {
                return ResponseEntity.ok(specGroupMapper.updateByPrimaryKey(specGroup));
            } else {

                return ResponseEntity.ok(specGroupMapper.insertSelective(specGroup));
            }
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    //删除
    public ResponseEntity findById(Long id) {
        try {
            if (id != null && id != 0) {

                specGroupMapper.deleteByPrimaryKey(id);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok("删除成功");
    }
}

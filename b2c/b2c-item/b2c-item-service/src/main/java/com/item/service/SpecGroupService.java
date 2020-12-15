package com.item.service;

import com.item.mapper.SpecParamMapper;
import com.mr.pojo.SpecParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SpecGroupService {
    @Resource
    private SpecParamMapper specParamMapper;

//查询
    public List<SpecParam> querySpecParams(Long gid,Long cid,Boolean searching,Boolean generic){
        SpecParam t = new SpecParam();
        t.setGroupId(gid);
        t.setCid(cid);
        t.setSearching(searching);
        t.setGeneric(generic);
        return this.specParamMapper.select(t);
    }

    /**
     * 增加
     * @param specParam
     * @return
     */

    public ResponseEntity saveOrUpdateSpecParam(SpecParam specParam){
        if(specParam != null){
            if(specParam.getId() !=null && specParam.getId() != 0){
                return ResponseEntity.ok(specParamMapper.updateByPrimaryKey(specParam));
            }else{

                return ResponseEntity.ok(specParamMapper.insertSelective(specParam));
            }
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    /**
     * 删除
     * @param id
     * @return
     */

    public ResponseEntity findById(Long id) {
        if (id != null && id != 0){
            return ResponseEntity.ok(specParamMapper.deleteByPrimaryKey(id));
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
}

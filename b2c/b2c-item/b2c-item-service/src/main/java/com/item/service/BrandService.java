package com.item.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.item.common.enums.ExceptionEnums;
import com.item.common.exception.MrException;
import com.item.common.utils.PageResult;
import com.item.mapper.BrandMapper;
import com.mr.pojo.Brand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class BrandService {

     @Autowired
    private BrandMapper brandMapper;

    public PageResult<Brand> queryByList(Integer page, Integer row, String searchKey, String sortBy, Boolean desc) {
        PageHelper.startPage(page,row);
        Example example =new Example(Brand.class);
        if(searchKey != null && !searchKey.equals("")){
            example.createCriteria().andLike("name","%"+searchKey+"%");
        }

        if(sortBy != null && !sortBy.equals("")){
            example.setOrderByClause(sortBy+ (desc?" asc":" desc"));
        }
        Page<Brand> pageData= (Page<Brand>) brandMapper.selectByExample(example);
        return new PageResult<Brand>(pageData.getTotal(),pageData.getResult());
    }

    //增加品牌/修改品牌
    @Transactional //多表之间控制事务
    public void addBrand(Brand brand, List<Long> cids) {
        if (brand.getId() != null && brand.getId() != 0) {
            //修改品牌
            brandMapper.updateByPrimaryKey(brand);
            //修改品牌,中间表,品牌下的分类; 先删后增
            //删除原来的分类
            brandMapper.deleteBrandById(brand.getId());
            //后增
            for (Long cid : cids) {
                brandMapper.insertBrandCategory(brand.getId(), cid);
            }
        } else {

            //新增品牌
            brandMapper.insertSelective(brand);
            //新增品牌,中间表,品牌下的分类
            for (Long cid : cids) {
                brandMapper.insertBrandCategory(brand.getId(), cid);
            }

        }

    }

    //删除
    public ResponseEntity findById(Long id) {
        try {
            if(id != null && id != 0){
                brandMapper.deleteByPrimaryKey(id);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new MrException(ExceptionEnums.ADD_UPDATE_CATEGORY_ERROR);
        }
        return ResponseEntity.ok("删除成功");
    }
    public List<Brand> queryBrandByCid(Long cid) {
        return this.brandMapper.queryBrandByCid(cid);
    }

    /**
     * 根据主键查询品牌
     * @param bid
     * @return
     */
    public Brand queryBrandListByBid(Long bid) {
        return brandMapper.selectByPrimaryKey(bid);
    }
}

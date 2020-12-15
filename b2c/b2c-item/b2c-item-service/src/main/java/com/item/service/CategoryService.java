package com.item.service;

import com.item.common.enums.ExceptionEnums;
import com.item.common.exception.MrException;
import com.item.mapper.CategoryMapper;
import com.mr.pojo.Category;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    //查询
    public List<Category> list(Long pid){
        Category cate = new Category();
        cate.setParentId(pid);
        List<Category> list = categoryMapper.select(cate);
        return list;
    }

    // 删除
    public void deleteCategory(Long id) {
        Category category=categoryMapper.selectByPrimaryKey(id);
        Category condition = new Category();
        condition.setParentId(category.getParentId());
        if (categoryMapper.select(condition).size()==1){
            Category temp = categoryMapper.selectByPrimaryKey(condition.getParentId());
            temp.setIsParent(false);
            categoryMapper.updateByPrimaryKeySelective(temp);
        }
        category.setId(id);
        categoryMapper.delete(category);
    }


    // 查询子节点
    public Category getIsParen(Long id) {
        return categoryMapper.selectByPrimaryKey(id);
    }

    //增加/修改
    public ResponseEntity save(Category category) {
        try {
            if (category != null){
                if (category.getId() != null && category.getId() != 0){
                    this.categoryMapper.updateByPrimaryKeySelective(category);
                }else {
                    this.categoryMapper.insertSelective(category);
                }
            }
        } catch (MrException e) {
            e.printStackTrace();
            throw new MrException(ExceptionEnums.SAVE_UPDATE_CATEGORY_ERROR);
        }
        return ResponseEntity.ok("操作成功");
    }

   //根据id修改
    public ResponseEntity<Category> findById(Long id) {
        if (id == null){
            throw new MrException(ExceptionEnums.SELECT_KEY_CATEGORY_ERROR);
        }
        Category category = this.categoryMapper.selectByPrimaryKey(id);
        return ResponseEntity.ok(category);
    }
 /*    public void update(Category category) {
      categoryMapper.updateByPrimaryKeySelective(category);
  }*/

    /**
     * 通过品牌id查询分类数据(用于品牌新增,把品牌新增到分类下面)
     * */

    public List<Category> queryCategoryByBid(Long bid) {
        return this.categoryMapper.queryCategoryBrand(bid);
    }

    /**
     * 根据分类id集合查询名称
     * @param ids
     * @return
     */
    public List<String> queryCateGoryNameListBycIds(List<Long> ids) {
       List<Category> list = categoryMapper.selectByIdList(ids);
      List<String> cateNameList =list.stream().map(category -> {
           return category.getName();
       }).collect(Collectors.toList());
        return cateNameList;
    }

    public Category queryCategoryById(Long id) {
      return  categoryMapper.selectByPrimaryKey(id);
    }

    public List<Category> queryCateGoryListBycIds(List<Long> ids) {
        return categoryMapper.selectByIdList(ids);
    }
}

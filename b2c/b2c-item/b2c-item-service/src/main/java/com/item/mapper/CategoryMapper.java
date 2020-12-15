package com.item.mapper;

import com.mr.pojo.Category;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@org.apache.ibatis.annotations.Mapper
public interface CategoryMapper extends tk.mybatis.mapper.common.Mapper<Category>,
        SelectByIdListMapper<Category,Long> {

    //查询品牌下拥有的分类
    /**
     * 通过品牌id查询分类数据
     * */
    @Select("SELECT c.* FROM tb_category c where c.id in(SELECT cb.category_id FROM tb_category_brand cb where cb.brand_id=#{bid})")
    public List<Category> queryCategoryBrand(@Param("bid") Long bid);
}

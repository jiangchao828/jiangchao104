package com.item.mapper;


import com.mr.pojo.Stock;
import org.apache.ibatis.annotations.Mapper;
import tk.mybatis.mapper.additional.idlist.DeleteByIdListMapper;

@Mapper
public interface StockMapper extends tk.mybatis.mapper.common.Mapper<Stock>,
        DeleteByIdListMapper<Stock,Long> {
}

package com.mr.api;

import com.item.common.utils.PageResult;
import com.mr.pojo.Brand;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RequestMapping("brand")
public interface BrandApi {
    @GetMapping(value="page")
    public PageResult queryBrandPage(
            @RequestParam(value="page",defaultValue = "1") Integer page,
            @RequestParam(value="row",defaultValue = "5") Integer row,
            @RequestParam(value="searchKey",required = false) String searchKey,
            @RequestParam(value="sortBy",required = false) String sortBy,
            @RequestParam(value="desc",required = false) Boolean desc
    );


    @PostMapping
    public Void addBrand(Brand brand, @RequestParam("cids") List<Long> cids);


    @PutMapping
    public Void updateBrand(Brand brand, @RequestParam("cids") List<Long> cids);

    //删除
    @DeleteMapping(value = "delete")
    public ResponseEntity deleteBrand(@RequestParam(value = "id") Long id);

    @GetMapping("cid/{cid}")
    public List<Brand> queryBrandByCategory(@PathVariable("cid") Long cid);

    /**
     * 根据主键查询
     * @param bid
     * @return
     */
    @GetMapping("bid")
    public Brand queryBrandByBid(@RequestParam("bid") Long bid);
}

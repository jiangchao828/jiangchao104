package com.item.controller;

import com.item.common.utils.PageResult;
import com.item.service.BrandService;
import com.mr.pojo.Brand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value="brand")
public class BrandController {

    @Autowired
    private BrandService brandService;

    @GetMapping(value="page")
    public ResponseEntity<PageResult> queryBrandPage(
            @RequestParam(value="page",defaultValue = "1") Integer page,
            @RequestParam(value="row",defaultValue = "5") Integer row,
            @RequestParam(value="searchKey",required = false) String searchKey,
            @RequestParam(value="sortBy",required = false) String sortBy,
            @RequestParam(value="desc",required = false) Boolean desc
    ){
        //请求service返回实体和前台传回的参数,以及分页数据
        PageResult<Brand> pageResult = brandService.queryByList(page,row,searchKey,sortBy,desc);
        if(pageResult == null || pageResult.getItems().size() == 0){
            //如果没有数据 执行异常代码
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(pageResult);
    }

     @PostMapping
     public ResponseEntity<Void> addBrand(Brand brand, @RequestParam("cids") List<Long> cids) {
         System.out.println(brand);
         System.out.println(cids);
         brandService.addBrand(brand,cids);
         return ResponseEntity.ok(null);

     }
    @PutMapping
    public ResponseEntity<Void> updateBrand(Brand brand, @RequestParam("cids") List<Long> cids) {
        brandService.addBrand(brand,cids);
        return ResponseEntity.ok(null);

    }

    //删除
    @DeleteMapping(value = "delete")
    public ResponseEntity deleteBrand(@RequestParam(value = "id") Long id){
        return this.brandService.findById(id);
    }

    /**
     * 根据分类查询品牌
     * @param cid
     * @return
     */
    @GetMapping("cid/{cid}")
    public ResponseEntity<List<Brand>> queryBrandByCategory(@PathVariable("cid") Long cid) {
        List<Brand> list = this.brandService.queryBrandByCid(cid);
        if(list == null){
            new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(list);
    }

    /**
     * 根据品牌id查询品牌
     * @param bid
     * @return
     */
    @GetMapping("bid")
    public ResponseEntity<Brand> queryBrandByBid(@RequestParam("bid") Long bid) {
        Brand brand = brandService.queryBrandListByBid(bid);
        return ResponseEntity.ok(brand);
    }
}

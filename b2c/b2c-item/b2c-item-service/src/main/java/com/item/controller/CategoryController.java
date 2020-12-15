package com.item.controller;

import com.item.common.enums.ExceptionEnums;
import com.item.common.exception.MrException;
import com.item.service.CategoryService;
import com.mr.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.spring.annotation.MapperScan;

import java.util.List;

@RestController
@RequestMapping("category")
@MapperScan("com.item.mapper")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;


    // 查询
    @GetMapping("list")
    public ResponseEntity<List<Category>> list(@RequestParam(value = "pid") Long pid){
        List<Category> list = categoryService.list(pid);
        if(list == null  || list.size() == 0){
            throw new MrException(ExceptionEnums.CATEGORY_NULL);
        }
        return ResponseEntity.ok(list);
    }

    // 查询子节点
    @GetMapping("getIsParen")
    public Category getIsParen(@RequestParam(value = "id") Long id){
        return  categoryService.getIsParen(id);
    }

    // 删除
    @DeleteMapping("delete")
    public  void  delete(Long id){
      categoryService.deleteCategory(id);
    }

    //增加
    @PostMapping(value = "save")
    public ResponseEntity<Category> saveCategory(@RequestBody Category category){
        return this.categoryService.save(category);
    }

    //修改
    @PutMapping(value = "save")
    public ResponseEntity<Category> editCategory(@RequestBody Category category){
        return this.categoryService.save(category);
    }

     // 修改回显
    @GetMapping(value = "findById")
    public ResponseEntity<Category> findById(@RequestParam("id") Long id){
        return this.categoryService.findById(id);
    }
    //查询品牌下的分类
    @GetMapping(value="queryCategoryBrand/{bid}")
    public ResponseEntity<List<Category>> queryCategoryBrand(@PathVariable("bid") Long bid){
        List<Category> list = categoryService.queryCategoryByBid(bid);
        if (list == null || list.size() < 1) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(list);
    }

    /**
     * 根据ids查询集合名称
     * @param ids
     * @return
     */
    @GetMapping("cids")
    public ResponseEntity<List<String>> queryCategoryNameBycIds(@RequestParam("ids") List<Long> ids){
        List<String> categoryList = categoryService.queryCateGoryNameListBycIds(ids);
        return ResponseEntity.ok(categoryList);
    }

    @GetMapping("cid")
    public ResponseEntity<Category> queryCategoryById(@RequestParam("id") Long id){
        Category category =categoryService.queryCategoryById(id);
        return ResponseEntity.ok(category);
    }

    @GetMapping("queryCateByIds")
    public ResponseEntity <List<Category>> queryCateByIds(@RequestParam("ids") List<Long> ids){
        List<Category> categoryList =categoryService.queryCateGoryListBycIds(ids);
        return ResponseEntity.ok(categoryList);
    }
}

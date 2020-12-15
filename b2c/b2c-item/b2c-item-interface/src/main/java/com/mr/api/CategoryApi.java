package com.mr.api;


import com.mr.pojo.Category;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;



@RequestMapping("category")
public interface CategoryApi {

    @GetMapping("list")
    public List<Category> list(@RequestParam(value = "pid") Long pid);


    @GetMapping("getIsParen")
    public Category getIsParen(@RequestParam(value = "id") Long id);


    @DeleteMapping("delete")
    public  void  delete(Long id);


    @PostMapping(value = "save")
    public ResponseEntity<Category> saveCategory(@RequestBody Category category);


    @PutMapping(value = "save")
    public ResponseEntity<Category> editCategory(@RequestBody Category category);



    @GetMapping(value = "findById")
    public ResponseEntity<Category> findById(@RequestParam("id") Long id);

    @GetMapping(value="queryCategoryBrand/{bid}")
    public List<Category> queryCategoryBrand(@PathVariable("bid") Long bid);

    /**
     * 根据ids查询集合名称
     * @param ids
     * @return
     */
    @GetMapping("cids")
    public List<String> queryCategoryNameBycIds(@RequestParam("ids") List<Long> ids);

    /**
     * 根据id查询单个对象
     * @param id
     * @return
     */
    @GetMapping("cid")
    public Category queryCategoryById(@RequestParam("id") Long id);

    /**
     * 批量查询分类
     * @param ids
     * @return
     */
    @GetMapping("queryCateByIds")
    public List<Category> queryCateByIds(@RequestParam("ids") List<Long> ids);


}

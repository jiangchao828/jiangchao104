package com.mr.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

@Service
public class FileStaticService {

    //查询model数据
    @Autowired
    private PageService pageService;

    //注入静态化模版
    @Autowired
    private TemplateEngine templateEngine;

    @Value("${b2c.thymeleaf.destPath}")
    private String destPath;// 保存的页面路径

    /**
     * 创建html页面
     * @param id
     * @throws Exception
     */
    public void createStaticHtml(Long id){
        // 创建上下文context
        Context context = new Context();
        // 把数据加入上下文context
        context.setVariables(this.pageService.queryItemInfo(id));
        // 创建最终文件输出流，指定文件夹目录，文件名 后缀 等
        File itemHtml = new File(destPath,id + ".html");
        //创建输出流 指定格式
        PrintWriter printWriter= null;
        try {
            printWriter = new PrintWriter(itemHtml,"UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // 利用thymeleaf模板引擎将上下文结合模版生成到指定到文件中
        templateEngine.process("item", context, printWriter);
    }
    public void deleteStaticHtml(Long id) throws Exception {
        // 创建最终文件输出流，指定文件夹目录，文件名 后缀 等
        File itemHtml = new File(destPath,id + ".html");
        itemHtml.deleteOnExit();
    }
}
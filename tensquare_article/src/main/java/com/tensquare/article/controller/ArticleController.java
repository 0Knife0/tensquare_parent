package com.tensquare.article.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.tensquare.article.pojo.Article;
import com.tensquare.article.service.ArticleService;
import com.tensquare.entity.PageResult;
import com.tensquare.entity.Result;
import com.tensquare.entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("article")
@CrossOrigin
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private RedisTemplate redisTemplate;

    // 文章点赞
    // http://127.0.0.1:9004/article/thumbup/{articleId} PUT
    @PutMapping("/thumbup/{articleId}")
    public Result thumbup(@PathVariable String articleId) {
        //TODO 使用JWT鉴权的方式获取当前用户的id
        String userId = "4";

        // 根据用户的id和文章id查询用户对此文章的点赞信息
        String key = "thumbup_article_" + userId + "_" + articleId;
        Object flag = redisTemplate.opsForValue().get(key);

        //判断查询到的结果如果为空
        if (flag == null) {
            // 如果为空证明没有点赞过，可以进行点赞操作
            articleService.thumbup(articleId, userId);
            // 点赞成功，保存点赞信息
            redisTemplate.opsForValue().set(key, 1);

            return new Result(true, StatusCode.OK, "点赞成功");
        } else {
            // 不为空，表示已经点过赞了，不可以重复点赞
            return new Result(false, StatusCode.REPERROR, "不能重复点赞");
        }
    }

    // 订阅或取消订阅文章作者
    // http://127.0.0.1:9004/article/subscribe POST
    @PostMapping("/subscribe")
    public Result subscribe(@RequestBody Map map) {
        //根据文章id，订阅文章作者，返回订阅状态，true表示订阅成功，false表示取消订阅成功
        Boolean flag = articleService.subscribe(map.get("articleId").toString()
                , map.get("userId").toString());
        if (flag) {
            return new Result(true, StatusCode.OK, "订阅成功");
        } else {
            return new Result(true, StatusCode.OK, "取消订阅成功");
        }
    }

    // GET /article 文章全部列表
    //@RequestMapping(method = RequestMethod.GET)
    @GetMapping
    public Result findAll() {
        List<Article> list = articleService.findAll();
        return new Result(true, StatusCode.OK, "查询成功", list);
    }

    // GET /article/{articleId} 根据ID查询文章
    @GetMapping(value = "{articleId}")
    public Result findById(@PathVariable String articleId) {
        Article article = articleService.findById(articleId);
        return new Result(true, StatusCode.OK, "查询成功", article);
    }

    // POST /article 增加文章
    @PostMapping
    public Result save(@RequestBody Article article) {
        articleService.save(article);
        return new Result(true, StatusCode.OK, "新增成功");
    }

    // PUT /article/{articleId} 修改文章
    @PutMapping(value = "{articleId}")
    public Result updateById(@PathVariable String articleId,
                             @RequestBody Article article) {
        //设置id
        article.setId(articleId);
        //执行修改
        articleService.updateById(article);
        return new Result(true, StatusCode.OK, "修改成功");
    }

    // DELETE /article/{articleId} 根据ID删除文章
    @DeleteMapping(value = "{articleId}")
    public Result deleteById(@PathVariable String articleId) {
        articleService.deleteById(articleId);
        return new Result(true, StatusCode.OK, "删除成功");
    }

    // POST /article/search/{page}/{size} 文章分页
    @PostMapping(value = "/search/{page}/{size}")
    // 之前接收文章数据，使用pojo，但是现在根据条件查询
    // 而所有的条件都需要进行判断，遍历pojo的所有属性需要使用反射的方式，成本较高，性能较低
    // 直接使用集合的方式遍历，这里接收数据改为Map集合
    public Result findByPage(@PathVariable Integer page,
                             @PathVariable Integer size,
                             @RequestBody Map<String, Object> map) {
        // 根据条件分页查询
        Page<Article> pageData = articleService.findByPage(map, page, size);
        // 封装分页返回对象
        PageResult<Article> pageResult = new PageResult<>(
                pageData.getTotal(), pageData.getRecords()
        );
        // 返回数据
        return new Result(true, StatusCode.OK, "查询成功", pageResult);
    }
}

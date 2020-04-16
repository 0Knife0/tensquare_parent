package com.tensquare.article.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.tensquare.article.client.NoticeClient;
import com.tensquare.article.dao.ArticleDao;
import com.tensquare.article.pojo.Article;
import com.tensquare.article.pojo.Notice;
import com.tensquare.util.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class ArticleService {

    @Autowired
    private ArticleDao articleDao;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private NoticeClient noticeClient;

    public List<Article> findAll() {
        return articleDao.selectList(null);
    }

    public Article findById(String articleId) {
        return articleDao.selectById(articleId);
    }

    public void save(Article article) {
        // TODO: 使用jwt鉴权获取当前用户的信息，用户id，也就是文章作者id
        String authorId = "3";
        article.setUserid(authorId);

        //使用分布式id生成器
        String id = idWorker.nextId() + "";
        article.setId(id);

        //初始化数据
        article.setVisits(0);   //浏览量
        article.setThumbup(0);  //点赞数
        article.setComment(0);  //评论数

        //新增
        articleDao.insert(article);

        // 新增文章后，创建消息，通知给订阅者

        // 获取订阅者信息
        String authorKey = "article_author_" + authorId;
        Set<String> set = redisTemplate.boundSetOps(authorKey).members();

        if (null != set && set.size() > 0){
            // 给订阅者创建一个消息通知
            Notice notice = null;
            for (String uid : set) {
                // 创建消息对象
                notice = new Notice();
                // 接收消息的用户id
                notice.setReceiverId(uid);
                // 文章作者id
                notice.setOperatorId(authorId);
                // 操作消息类型（评论、点赞）等
                notice.setAction("publish");
                // 操作的对象（文章、评论）等
                notice.setTargetType("article");
                // 操作对象的id
                notice.setTargetId(id);
                // 通知类型
                notice.setType("sys");

                noticeClient.save(notice);
            }
        }
    }

    public void updateById(Article article) {
        // 根据主键id修改
        articleDao.updateById(article);
        /*// 根据条件修改
        // 创建条件对象
        EntityWrapper<Article> wrapper = new EntityWrapper<>();
        // 设置条件
        wrapper.eq("id", article.getId());
        articleDao.update(article, wrapper);*/
    }

    public void deleteById(String articleId) {
        articleDao.deleteById(articleId);
    }

    public Page<Article> findByPage(Map<String, Object> map, Integer page, Integer size) {
        // 设置查询条件
        EntityWrapper<Article> wrapper = new EntityWrapper<>();
        Set<String> keySet = map.keySet();
        for (String key : keySet) {
            /*if (map.get(key) != null) {
                wrapper.eq(key, map.get(key));
            }*/
            // 第一个参数是否把后面的条件加入到查询条件中
            // 和上面的if判断的写法是一样的效果，实现动态sql
            wrapper.eq(map.get(key) != null, key, map.get(key));
        }
        // 设置分页参数
        Page<Article> pageData = new Page<>(page, size);
        // 执行查询
        // 第一个是分页参数，第二个是查询条件
        List<Article> list = articleDao.selectPage(pageData, wrapper);
        pageData.setRecords(list);
        // 返回
        return pageData;
    }

    public Boolean subscribe(String articleId, String userId) {
        //根据文章id查询文章作者id
        String authorId = articleDao.selectById(articleId).getUserid();

        // 存放用户订阅信息集合的key，里面存放作者id
        String authorKey = "article_author_" + authorId;
        // 存放作者订阅者信息集合的key，里面存放用户id
        String userKey = "article_subscribe_" + userId;

        Boolean flag = redisTemplate.boundSetOps(userKey).isMember(authorId);

        // 查询该用户的订阅关系，是否有订阅该作者
        if (flag) {
            // 如果有订阅就取消订阅
            // 从用户订阅信息集合中，删除作者id
            redisTemplate.boundSetOps(userKey).remove(authorId);
            // 从作者订阅者信息集合中，删除用户id
            redisTemplate.boundSetOps(authorKey).remove(userId);
            return false;
        } else {
            // 如果没有订阅就订阅
            // 从用户订阅信息集合中，增加作者id
            redisTemplate.boundSetOps(userKey).add(authorId);
            // 从作者订阅者信息集合中，增加用户id
            redisTemplate.boundSetOps(authorKey).add(userId);
            return true;
        }
    }
}

package com.tensquare.article.service;

import com.tensquare.article.pojo.Comment;
import com.tensquare.article.repository.CommentRepository;
import com.tensquare.util.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<Comment> findAll() {
        List<Comment> list = commentRepository.findAll();

        return list;
    }

    public Comment findById(String commentId) {
        //防止空指针异常
        /*Optional<Comment> optional = commentRepository.findById(commentId);
        if (optional.isPresent()) {
            return optional.get();
        }
        return null;*/

        Comment comment = commentRepository.findById(commentId).get();

        return comment;
    }

    public void save(Comment comment) {
        /*
        如果直接使用分布式id生成器生成MongoDB的主键，MongoDB插入速度会受到极大影响
        可以考虑扔使用MongoDB自己生成主键，但不同机器中的MongoDB主键可能相同
        在数据中新增一列用来保存分布式id生成器生成的分布式id，并创建索引，可保证查询速度
         */
        // 分布式id生成器生成id
        String id = idWorker.nextId() + "";
        comment.set_id(id);
        // 初始化点赞数据、发布时间等
        comment.setThumbup(0);
        comment.setPublishdate(new Date());
        // 保存数据
        commentRepository.save(comment);
    }

    public void updateById(Comment comment) {
        // 使用的是MongoRepository的方法
        // 其中save方法，主键如果存在，执行修改方法，如果不存在执行新增
        commentRepository.save(comment);
    }

    public void deleteById(String commentId) {
        commentRepository.deleteById(commentId);
    }

    public List<Comment> findByArticleId(String articleId) {
        List<Comment> list = commentRepository.findByArticleid(articleId);

        return list;
    }

    public void thumbup(String commentId) {
        /* 该操作可能会有线程安全问题
        // 根据评论id查询评论数据
        Comment comment = commentRepository.findById(commentId).get();
        // 对评论点赞数据加1
        comment.setThumbup(comment.getThumbup() + 1);
        // 保存修改数据
        commentRepository.save(comment);*/

        // 点赞功能优化
        // 封装修改的条件
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(commentId));

        // 封装修改的数值
        Update update = new Update();
        // 使用inc列值增长
        update.inc("thumbup", 1);

        // 直接修改数据
        // 第一个参数是修改的条件
        // 第二个参数是修改的数值
        // 第三个参数是MongoDB的集合名称
        mongoTemplate.updateFirst(query, update, "comment");
    }
}

package com.tensquare.article.pojo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

@Data
@Document("comment")
public class Comment implements Serializable {
    @Id
    private String _id;
    private String articleid;   // 评论对应文章id
    private String content;     // 评论内容
    private String userid;      // 评论人id
    private String parentid;    // 评论评论的id
    private Date publishdate;   // 评论发布时间
    private Integer thumbup;    // 点赞数
}

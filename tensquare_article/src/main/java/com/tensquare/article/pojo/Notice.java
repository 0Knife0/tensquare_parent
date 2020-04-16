package com.tensquare.article.pojo;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@TableName("tb_notice")
@Data
public class Notice implements Serializable {
    @TableId(type = IdType.INPUT)
    private String id;          // 用户id

    private String receiverId;  // 接收消息用户的ID
    private String operatorId;  // 进行操作用户的ID

    @TableField(exist = false)
    private String operatorName;//进行操作的用户昵称
    private String action;      // 操作类型（评论，点赞等）
    private String targetType;  // 被操作的对象，例如文章，评论等

    @TableField(exist = false)
    private String targetName;  //对象名称或简介
    private String targetId;    // 被操作对象的id，例如文章的id，评论的id
    private Date createtime;    // 发表日期
    private String type;        // 通知类型 （sys系统消息 user用户消息）
    private String state;       // 消息状态（0 未读，1 已读）
}

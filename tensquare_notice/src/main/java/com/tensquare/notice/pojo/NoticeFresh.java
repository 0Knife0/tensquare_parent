package com.tensquare.notice.pojo;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;

@TableName("tb_notice_fresh")
@Data
public class NoticeFresh implements Serializable {

    private String userId;      //用户id
    private String noticeId;    //通知id
}

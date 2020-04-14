package com.tensquare.notice.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.tensquare.entity.Result;
import com.tensquare.notice.client.ArticleClient;
import com.tensquare.notice.client.UserClient;
import com.tensquare.notice.dao.NoticeDao;
import com.tensquare.notice.dao.NoticeFreshDao;
import com.tensquare.notice.pojo.Notice;
import com.tensquare.notice.pojo.NoticeFresh;
import com.tensquare.util.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NoticeService {

    @Autowired
    private NoticeDao noticeDao;

    @Autowired
    private NoticeFreshDao noticeFreshDao;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private ArticleClient articleClient;

    @Autowired
    private UserClient userClientl;

    // 查询消息的相关信息
    private void getNoticeInfo(Notice notice) {
        // 获取用户信息
        Result userResult = userClientl.selectById(notice.getOperatorId());
        HashMap userMap = (HashMap) userResult.getData();
        // 将用户昵称设置到通知中
        notice.setOperatorName(userMap.get("nickname").toString());
        // 获取文章信息
        Result articleResult = articleClient.findById(notice.getTargetId());
        HashMap articleMap = (HashMap) articleResult.getData();
        // 将文章名称设置到通知中
        notice.setTargetName(articleMap.get("title").toString());
    }

    public Notice fidById(String id) {
        Notice notice = noticeDao.selectById(id);
        // 设置消息相关信息
        getNoticeInfo(notice);
        return notice;
    }

    public Page<Notice> findByPage(Notice notice, Integer page, Integer size) {
        // 封装分页对象
        Page<Notice> pageData = new Page<>(page, size);
        // 执行分页查询
        List<Notice> noticeList = noticeDao.selectPage(pageData, new EntityWrapper<>(notice));
        // 设置消息相关信息
        for (Notice n : noticeList) {
            getNoticeInfo(n);
        }
        // 封装结果到分页对象
        pageData.setRecords(noticeList);
        // 返回
        return pageData;
    }

    public void save(Notice notice) {
        // 设置消息id
        String id = idWorker.nextId() + "";
        notice.setId(id);

        // 初始化初始值
        //设置状态 0表示未读 1表示已读
        notice.setState("0");
        notice.setCreatetime(new Date());

        noticeDao.insert(notice);

        //待推送消息入库，新消息提醒
        NoticeFresh noticeFresh = new NoticeFresh();
        noticeFresh.setNoticeId(id); // 消息id
        noticeFresh.setUserId(notice.getReceiverId()); // 待通知用户id
        noticeFreshDao.insert(noticeFresh);
    }

    public void updateById(Notice notice) {
        noticeDao.updateById(notice);
    }

    public Page<NoticeFresh> freshPage(String userId, Integer page, Integer size) {
        // 封装查询条件
        NoticeFresh noticeFresh = new NoticeFresh();
        noticeFresh.setUserId(userId);
        // 创建分页对象
        Page<NoticeFresh> pageData = new Page<>(page, size);
        // 执行查询
        List<NoticeFresh> noticeFreshList = noticeFreshDao.selectPage(pageData, new EntityWrapper<>(noticeFresh));
        // 设置查询结果集到分页对象中
        pageData.setRecords(noticeFreshList);
        // 返回结果
        return pageData;
    }

    public void freshDelete(NoticeFresh noticeFresh) {
        noticeFreshDao.delete(new EntityWrapper<>(noticeFresh));
    }
}

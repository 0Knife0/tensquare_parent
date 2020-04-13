package com.tensquare.notice.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.tensquare.entity.PageResult;
import com.tensquare.entity.Result;
import com.tensquare.entity.StatusCode;
import com.tensquare.notice.pojo.Notice;
import com.tensquare.notice.pojo.NoticeFresh;
import com.tensquare.notice.service.NoticeService;
import org.aspectj.weaver.ast.Not;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("notice")
@CrossOrigin
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    // 1. 根据id查询消息通知
    // http://127.0.0.1:9014/notice/{id} GET
    @GetMapping(value = "{id}")
    public Result findById(@PathVariable String id) {
        Notice notice = noticeService.fidById(id);
        return new Result(true, StatusCode.OK, "查询成功", notice);
    }

    // 2. 根据条件分页查询消息通知
    // http://127.0.0.1:9014/notice/search/{page}/{size} POST
    @PostMapping(value = "search/{page}/{size}")
    public Result findByPage(@PathVariable Integer page,
                             @PathVariable Integer size,
                             @RequestBody Notice notice) {
        // 根据分页查询
        Page<Notice> pageData = noticeService.findByPage(notice, page, size);
        // 封装分页返回对象
        PageResult<Notice> pageResult = new PageResult<>(
                pageData.getTotal(), pageData.getRecords()
        );
        // 返回数据
        return new Result(true, StatusCode.OK, "查询成功", pageResult);
    }

    // 3. 新增通知
    // http://127.0.0.1:9014/notice POST
    @PostMapping
    public Result save(@RequestBody Notice notice) {
        noticeService.save(notice);
        return new Result(true, StatusCode.OK, "保存成功");
    }

    // 4. 修改通知
    // http://127.0.0.1:9014/notice PUT
    @PutMapping
    public Result updateById(@RequestBody Notice notice) {
        noticeService.updateById(notice);
        return new Result(true, StatusCode.OK, "修改成功");
    }

    // 5. 根据用户id查询该用户的待推送消息（新消息）
    // http://127.0.0.1:9014/notice/fresh/{userId}/{page}/{size} GET
    @GetMapping(value = "fresh/{userId}/{page}/{size}")
    public Result freshPage(@PathVariable String userId,
                            @PathVariable Integer page,
                            @PathVariable Integer size) {
        Page<NoticeFresh> pageData = noticeService.freshPage(userId, page, size);
        // 封装结果集
        PageResult<NoticeFresh> pageResult = new PageResult<>(
                pageData.getTotal(), pageData.getRecords()
        );
        // TODO 存在一个用户可以查询任意用户的情况，需要进行权限验证分为管理员和普通用户的情况
        return new Result(true, StatusCode.OK, "查询成功", pageResult);
    }

    // 6. 删除待推送消息（新消息）
    // http://127.0.0.1:9014/notice/fresh DELETE
    @DeleteMapping(value = "fresh")
    public Result freshDelete(@RequestBody NoticeFresh noticeFresh) {
        noticeService.freshDelete(noticeFresh);
        return new Result(true, StatusCode.OK, "删除成功");
    }
}

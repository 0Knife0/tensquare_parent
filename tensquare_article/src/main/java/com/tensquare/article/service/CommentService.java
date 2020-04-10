package com.tensquare.article.service;

import com.tensquare.article.pojo.Comment;
import com.tensquare.article.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    public List<Comment> findAll() {
        List<Comment> list = commentRepository.findAll();

        return list;
    }

    public Comment findById(String commentId) {
        Comment comment = commentRepository.findById(commentId).get();

        return comment;
    }
}

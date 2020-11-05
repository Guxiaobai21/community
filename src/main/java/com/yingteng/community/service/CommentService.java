package com.yingteng.community.service;

import com.yingteng.community.dao.CommentMapper;
import com.yingteng.community.entity.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;

    public List<Comment> findCommentByEntity(int entityType, int enrityId, int offset, int limit){
        return commentMapper.selectCommentByEntity(entityType,enrityId,offset,limit);
    }

    public int findCommentCount(int entityType, int entityId){
        return commentMapper.selectCountByEntity(entityType,entityId);
    }

}

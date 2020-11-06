package com.yingteng.community.service;

import com.yingteng.community.dao.CommentMapper;
import com.yingteng.community.dao.DiscussPostMapper;
import com.yingteng.community.entity.Comment;
import com.yingteng.community.entity.DiscussPost;
import com.yingteng.community.util.CommunityContant;
import com.yingteng.community.util.SensitiveFillter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommentService implements CommunityContant {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private SensitiveFillter sensitiveFillter;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    public List<Comment> findCommentByEntity(int entityType, int enrityId, int offset, int limit){
        return commentMapper.selectCommentByEntity(entityType,enrityId,offset,limit);
    }

    public int findCommentCount(int entityType, int entityId){
        return commentMapper.selectCountByEntity(entityType,entityId);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int addComment(Comment comment){
        if (comment == null){
            throw new IllegalArgumentException("参数不能为空！");
        }

        //添加评论
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveFillter.filter(comment.getContent()));
        int rows = commentMapper.insertComment(comment);

        //更新帖子评论数量
        if (comment.getEntityType() == ENTITY_TYPE_POST){
            int count = commentMapper.selectCountByEntity(comment.getEntityType(), comment.getEntityId());
            discussPostMapper.updateCommentCount(comment.getId(), count);
        }

        return rows;
    }

}

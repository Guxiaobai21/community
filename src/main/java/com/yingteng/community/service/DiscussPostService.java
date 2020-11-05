package com.yingteng.community.service;

import com.yingteng.community.dao.DiscussPostMapper;
import com.yingteng.community.entity.DiscussPost;
import com.yingteng.community.util.SensitiveFillter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class DiscussPostService {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private SensitiveFillter sensitiveFillter;

        public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit){
            return discussPostMapper.selectDiscussPosts(userId,offset,limit);
    }

    public int findDiscussPostRows(int userId){
        return discussPostMapper.selectDiscussPostRows(userId);
    }


    public int addDiscussPost(DiscussPost post){
        if (post == null){
            throw new IllegalArgumentException("参数不能为空!");
        }
        //  转义HTML标记
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));
        //  过滤敏感词
        post.setTitle(sensitiveFillter.filter(post.getTitle()));
        post.setContent(sensitiveFillter.filter(post.getContent()));

        return discussPostMapper.insertDiscussPost(post);
    }

    public DiscussPost findDiscussPostById(int id){
            return discussPostMapper.selectDiscussPostById(id);
    }
}

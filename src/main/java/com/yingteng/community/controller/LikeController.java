package com.yingteng.community.controller;

import com.yingteng.community.Event.EventProducer;
import com.yingteng.community.entity.Event;
import com.yingteng.community.entity.User;
import com.yingteng.community.service.LikeService;
import com.yingteng.community.util.CommunityContant;
import com.yingteng.community.util.CommunityUtil;
import com.yingteng.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.RequestPath;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeController implements CommunityContant {

    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private LikeService likeService;
    @Autowired
    private EventProducer eventProducer;

    @RequestMapping(path = "/like", method = RequestMethod.POST)
    @ResponseBody
    public String like(int entityType, int entityId, int entityUserId, int postId){
        User user = hostHolder.getUser();

        // 点赞或取消赞
        likeService.like(user.getId(), entityType, entityId, entityUserId);

        // 数量
        long count = likeService.findEntityLikeCount(entityType, entityId);
        // 状态
        int status = likeService.findEntityLikeStatus(user.getId(), entityType, entityId);

        Map<String, Object> map = new HashMap<>();
        map.put("likeCount", count);
        map.put("likeStatus", status);

        // 触发点赞事件
        if (status == 1) {
            Event event = new Event();
            event.setTopic(TOPIC_LIKE)
                    .setUserId(hostHolder.getUser().getId())
                    .setEntityType(entityType)
                    .setEntityId(entityId)
                    .setEntityUserId(entityUserId)
                    .setData("postId", postId);
            eventProducer.fireEvent(event);
        }

        return CommunityUtil.getJSONString(0, null, map);
    }
}

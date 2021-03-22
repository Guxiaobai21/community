package com.yingteng.community.service;

import com.yingteng.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

@Service
public class LikeService {

    @Autowired
    private RedisTemplate redisTemplate;


    // 点赞
    public void like(int userId, int entityType, int entityId, int entityUserId){
        // Redis 事务
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
                String userLikeKey = RedisKeyUtil.getUserLikeKey(entityUserId);

                boolean isMember = operations.opsForSet().isMember(entityLikeKey, userId);

                // 开始，isMemeber在事务外查询才会生效（由于事务中的语句都存在一个队列中）
                operations.multi();
                if (isMember){
                    // 取消赞，个人获赞数减一
                    operations.opsForSet().remove(entityLikeKey, userId);
                    operations.opsForValue().decrement(userLikeKey);
                }else{
                    // 点赞，个人获赞数加一
                    operations.opsForSet().add(entityLikeKey, userId);
                    operations.opsForValue().increment(userLikeKey);
                }

                return operations.exec();// 提交
            }
        });

    }

    // 查询点赞数量
    public long findEntityLikeCount(int entityType, int entityId){
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().size(entityLikeKey);
    }

    // 查询某人点赞状态
    public int findEntityLikeStatus(int userId, int entityType, int entityId){
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().isMember(entityLikeKey, userId)? 1 : 0 ;
    }

    // 查询某个用户获得赞的数量
    public int findUserLikeCount(int userId){
        String userLikeKey = RedisKeyUtil.getUserLikeKey(userId);
        Integer likeCount = (Integer) redisTemplate.opsForValue().get(userLikeKey);
        return likeCount == null? 0:likeCount.intValue();
    }


}

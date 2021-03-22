package com.yingteng.community.util;

public class RedisKeyUtil {

    public static final String SPLIT = ":";
    public static final String PREFIX_ENTITY_LIKE = "like:entity";
    public static final String PREFIX_USER_LIKE = "like:user";
    public static final String PREFIX_FOLLOWEE = "folLowee";
    public static final String PREFIX_FOLLOWER = "folLower";


    /**
     * 获取某个实体的赞 redis Key
     * @param entityType 实体类型：2
     * @param entityId 实体ID：用户id
     * @return
     */
    // like:entity:entityType:entityId -> set(userId)
    public static String getEntityLikeKey(int entityType, int entityId){
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    /**
     * 获取某个用户的赞
     * @param userId 用户Id
     * @return
     */
    // like:user:userId -> int
    public static String getUserLikeKey(int userId){
        return PREFIX_USER_LIKE + SPLIT + userId;
    }


    /**
     * 某个用户关注的实体
     * @param userId 用户Id
     * @param entityType 实体类型
     * @return
     */
    // followee:userId:entityType -> zset(entityId, now)
    public static String getFolloweeKey(int userId, int entityType){
        return PREFIX_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    /**
     * 某个实体拥有的粉丝用户
     * @param entityType 实体类型
     * @param entityId 实体Id
     * @return
     */
    // follwer:entityType:entityId -> zset(userId, now)
    public static String getFollowerKey(int entityType, int entityId){
        return PREFIX_FOLLOWER + SPLIT + entityType + SPLIT + entityId;
    }

}

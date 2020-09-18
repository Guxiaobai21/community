package com.yingteng.community.dao;

import com.yingteng.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper
public interface DiscussPostMapper {

    /**
     * 查询社区帖子信息
     * @param userId 用户Id
     * @param offset 分页的起始行
     * @param limit 分页的最大访问数
     * @return
     */
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit);

    /**
     * 查询帖子条数
     * @param userId
     * @return
     */
    //@Param注解用于给参数取别名
    //如果只有一个参数，并且<if>里使用，则必须加别名。
    int selectDiscussPostRows(@Param("userId") int userId);

}

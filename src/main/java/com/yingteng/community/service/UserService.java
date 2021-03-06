package com.yingteng.community.service;

import com.yingteng.community.dao.LoginTicketMapper;
import com.yingteng.community.dao.UserMapper;
import com.yingteng.community.entity.LoginTicket;
import com.yingteng.community.entity.User;
import com.yingteng.community.util.*;
import io.lettuce.core.RedisURI;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.*;

@Service
public class UserService implements CommunityContant {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    public User findUserById(int userId){
        User user = getCache(userId);
        if (user == null){
            user = initCache(userId);
        }
        return user;
    }

    /**
     * 注册
     * @param user
     * @return
     */
    public Map<String, Object> register(User user) {
        Map<String, Object> map =new HashMap<>();

        //空值处理
        if (user == null){
            throw new IllegalArgumentException("参数不能为空！");
        }
        //业务参数处理
        if (StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg", "账号不能为空！");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg", "密码不能为空！");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg", "邮箱不能为空！");
            return map;
        }

        //验证账号
        User u = userMapper.selectByName(user.getUsername());
        if (u != null){
            map.put("usernameMsg", "该账号已经存在！");
            return map;
        }

        //验证邮箱
        u = userMapper.selectByEmail(user.getEmail());
        if (u != null){
            map.put("emailMsg", "该邮箱已经被注册！");
            return map;
        }

        //注册用户
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        //激活邮件
        //使用thymeleaf下的Context装载内容
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        //URL格式示例：http://localhost:8080/community/activation/101/code
        String url = domain + contextPath +"/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);
        //通过thymeleaf下的Template模板装载内容，然后转成字符串返回
        String content = templateEngine.process("/mail/activation", context);
        //发送邮件,参数：邮箱、主题、内容
        mailClient.sendMail(user.getEmail(), "激活账号", content);
        return map;
    }

    /**
     * 判断激活状态
     * @param userId 用户ID
     * @param code 激活码
     * @return
     */
    public int activation(int userId, String code){
        User user = userMapper.selectById(userId);
        if (user.getStatus() == 1){
            return ACTIVATION_REPEAT;
        }else if (user.getActivationCode().equals(code)){
//            userMapper.updateStatus(userId, 1);
            clearCache(userId);
            return ACTIVATION_SUCCESS;
        }else{
            return ACTIVATION_FAILURE;
        }
    }

    /**
     * 登录
     * @param username
     * @param password
     * @param expiredSeconds
     * @return
     */
    public Map<String, Object> login(String username, String password, int expiredSeconds){
        Map<String, Object> map = new HashMap<>();

        //空值处理
        if (StringUtils.isBlank(username)){
            map.put("usernameMsg", "账号不能为空！");
            return map;
        }
        if (StringUtils.isBlank(password)){
            map.put("passwordMsg", "密码不能为空！");
        }

        //验证账号
        User user = userMapper.selectByName(username);
        if (user == null){
            map.put("usernameMsg", "该账号不存在！");
            return map;
        }

        //验证状态
        if (user.getStatus() == 0){
            map.put("usernameMsg", "该账号未激活！");
            return map;
        }

        //验证密码
        if (!user.getPassword().equals(CommunityUtil.md5(password + user.getSalt()))){
            map.put("passwordMsg", "密码不正确！");
            return map;
        }
        //生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));
//        loginTicketMapper.insertLoginTicket(loginTicket);

        // 将登录凭证存入redis
        String ticketKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(ticketKey, loginTicket);

        map.put("ticket", loginTicket.getTicket());
        return map;
    }


    public void logout(String ticket) {
//        loginTicketMapper.updateStatus(ticket, 1);
        String ticketKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(ticketKey);
        loginTicket.setStatus(1);
        redisTemplate.opsForValue().set(ticketKey, loginTicket);
    }

    public LoginTicket findLoginTicket(String ticket) {
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(redisKey);
        return loginTicket;
    }

    public int updateHeader(int userId, String headerUrl) {
//        return userMapper.updateHeader(userId, headerUrl);
        int rows = userMapper.updateHeader(userId, headerUrl);
        clearCache(userId);
        return rows;
    }

    public Map<String, Object> updatePassword (String oldPassword, String newPassword, String confirmPassword, String ticket){
        HashMap<String, Object> map = new HashMap<>();
        //参数校验
        if (StringUtils.isBlank(oldPassword)){
            map.put("oldPasswordMsg", "密码不能为空！");
            return map;
        }
        if (StringUtils.isBlank(newPassword)){
            map.put("newPasswordMsg", "新密码不能为空！");
            return map;
        }
        if (!newPassword.equals(confirmPassword)){
            map.put("confirmPasswordMsg", "确认密码不一致！");
            return map;
        }
        //账号验证
        User user = hostHolder.getUser();
        if (!user.getPassword().equals(CommunityUtil.md5(oldPassword + user.getSalt()))){
            map.put("oldPasswordMsg", "密码不正确！");
            return map;
        }
        //更新密码
        userMapper.updatePassword(user.getId(), CommunityUtil.md5(newPassword + user.getSalt()));
        //更改用户凭证状态
        loginTicketMapper.updateStatus(ticket,1);
        hostHolder.clear();
        return map;
    }

    public User findUserByName(String name){
        return userMapper.selectByName(name);
    }

    // 1.优先从缓存中取值
     public User getCache(int userId){
        String redisKey = RedisKeyUtil.getUserKey(userId);
        return (User) redisTemplate.opsForValue().get(redisKey);
     }
    // 2.取不到时初始化缓存数据
    public User initCache(int userId){
        String redisKey = RedisKeyUtil.getUserKey(userId);
        User user = userMapper.selectById(userId);
        redisTemplate.opsForValue().set(redisKey, user);
        return user;
    }
    // 3.数据变更时清除缓存数据
    public void clearCache(int userId){
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(redisKey);
    }

}

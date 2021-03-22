package com.yingteng.community;

import jdk.nashorn.internal.runtime.regexp.joni.constants.OPSize;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.*;
import org.springframework.test.context.ContextConfiguration;

import javax.swing.plaf.PanelUI;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class RedisTest {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 字符串类型 ： key-value
     */
    @Test
    public void testStrings(){
        String redisKey = "test:string";

        redisTemplate.opsForValue().set(redisKey, 1);
//        redisTemplate.opsForValue().set(redisKey, 1, 10, TimeUnit.SECONDS); 指定10秒后删除

        System.out.println(redisTemplate.opsForValue().get(redisKey));
        System.out.println(redisTemplate.opsForValue().increment(redisKey));// 自增
        System.out.println(redisTemplate.opsForValue().decrement(redisKey));// 自减
    }

    /**
     * Hash类型 ： hashKey下有多个key-value
     */
    @Test
    public void testHash(){
        String redisKey = "test:hash";

        redisTemplate.opsForHash().put(redisKey, "id", 1);
        redisTemplate.opsForHash().put(redisKey, "username", "顧同學");
        redisTemplate.opsForHash().put(redisKey, "sex", "male");

        System.out.println(redisTemplate.opsForHash().get(redisKey, "id"));
        System.out.println(redisTemplate.opsForHash().get(redisKey, "username"));
        System.out.println(redisTemplate.opsForHash().get(redisKey, "sex"));
    }


    /**
     * 列表类型 : 一个key下多个值， 可支持队列和栈的数据结构
     */
    @Test
    public void testList(){
        String redisKey = "test:list";

        redisTemplate.opsForList().leftPush(redisKey, 101);
        redisTemplate.opsForList().leftPush(redisKey, 102);
        redisTemplate.opsForList().leftPush(redisKey, 103);

        // 返回指定list的大小
        System.out.println(redisTemplate.opsForList().size(redisKey));
        // 返回指定list的指定索引上的值
        System.out.println(redisTemplate.opsForList().index(redisKey, 1));
        // 返回指定list的指定范围内的索引上的值
        System.out.println(redisTemplate.opsForList().range(redisKey, 0, 2));

        // 从左弹出一个元素
        System.out.println(redisTemplate.opsForList().leftPop(redisKey));
        // 从右弹出一个元素
        System.out.println(redisTemplate.opsForList().rightPop(redisKey));
        // 从左弹出一个元素（最后一个）
        System.out.println(redisTemplate.opsForList().leftPop(redisKey));

    }

    /**
     * 集合（set）类型： 一个key下多个值，支持随机弹出，元素唯一
     */
    @Test
    public void testSet(){
        String redisKey = "test:set";

        // 可存一个或多个
        redisTemplate.opsForSet().add(redisKey, "张飞", "关羽", "刘备");

        // 返回指定key下的集合大小
        System.out.println(redisTemplate.opsForSet().size(redisKey));
        // 随机返回一个元素
        System.out.println(redisTemplate.opsForSet().pop(redisKey));
        // 统计指定key下的元素内容
        System.out.println(redisTemplate.opsForSet().members(redisKey));
    }

    @Test
    public void testSortedSet(){
        String redisKey = "test:sortedSet";

        // 键-值-分数
        redisTemplate.opsForZSet().add(redisKey, "薇恩", 90);
        redisTemplate.opsForZSet().add(redisKey, "亚索", 70);
        redisTemplate.opsForZSet().add(redisKey, "剑姬", 30);

        // 返回集合有多少个数据
        System.out.println(redisTemplate.opsForZSet().zCard(redisKey));
        // 查询某个value的分数
        System.out.println(redisTemplate.opsForZSet().score(redisKey, "亚索"));
        // 查询某个value在set中所处的排名，根据score从小到大排序
        System.out.println(redisTemplate.opsForZSet().rank(redisKey, "剑姬"));
        // 查询某个value在set中所处的排名，根据score从大到小排序
        System.out.println(redisTemplate.opsForZSet().reverseRank(redisKey, "剑姬"));
        // 查询某范围内的数据, [0,2]表示前3名，默认从小到大
        System.out.println(redisTemplate.opsForZSet().range(redisKey, 0, 2));
        // 查询某范围内的数据, [0,2]表示倒数3名，从大到小
        System.out.println(redisTemplate.opsForZSet().reverseRange(redisKey, 0, 2));

    }

    @Test
    public void testKey(){

        // 删除指定key
        redisTemplate.delete("test:string");
        redisTemplate.delete("test:list");
        redisTemplate.delete("test:set");
        redisTemplate.delete("test:sortedSet");
        // 查询是否包含指定key
        System.out.println(redisTemplate.hasKey("test:string"));
        // 设置指定key的过期时间(10秒)
        redisTemplate.expire("test:string", 10, TimeUnit.SECONDS);

    }

    @Test
    public void BoundOperation(){
        String redisKey = "test:boundOperation";

        BoundValueOperations operations = redisTemplate.boundValueOps(redisKey);

        // 绑定key后的快捷操作
        operations.set(1);
        operations.increment();
        operations.increment();
        operations.increment();
        System.out.println(operations.get());

    }

    @Test
    public void testTransational(){
        Object obj = redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String redisKey = "test:transation";
                // 开启事务
                redisOperations.multi();
                redisOperations.opsForSet().add(redisKey,"亚索");
                redisOperations.opsForSet().add(redisKey,"薇恩");
                redisOperations.opsForSet().add(redisKey,"剑姬");

                // 此时的查询是无效的，因为在事务中还未提交，上面的add操作还未执行
                System.out.println(redisOperations.opsForSet().members(redisKey));
                return redisOperations.exec(); // 提交事务，在提交之前，所以的操作都会被放到一个队列中，提交之后才开始执行。（所以在期间进行查询是查不到的）
            }
        });
        System.out.println(obj);
    }

    @Test
    public void testZset(){
        String rankWeekKey = "rank:2021-3-8:2021-3-14";

//        map.forEach((key,value) ->{
//            System.out.println("key:"+ key + " value:" + value);
//        });
//        redisTemplate.opsForZSet().incrementScore(rankWeekKey, "345", 12);
//        redisTemplate.opsForZSet().incrementScore(rankWeekKey, "456", 1234);

/*        Set<ZSetOperations.TypedTuple> set = redisTemplate.opsForZSet().reverseRangeWithScores(rankWeekKey,0,2);
        Iterator<ZSetOperations.TypedTuple> iterator = set.iterator();
        while (iterator.hasNext()){
            ZSetOperations.TypedTuple next = iterator.next();
            System.out.println("value:"+next.getValue()+" score:"+next.getScore());
        }*/

//        System.out.println(redisTemplate.opsForZSet().reverseRangeWithScores(rankWeekKey,0,2));;
//        System.out.println(redisTemplate.opsForZSet().zCard(rankWeekKey));
        System.out.println(redisTemplate.opsForHash().hasKey(rankWeekKey, "11"));
    }

    @Test
    public void testHashMap(){
        String hashKey = "fake";
        String itemKey = "item";
//        Map<String, Object> hashmap = new HashMap<>();
//        hashmap.put("fake", 1);
/*
//        redisTemplate.opsForHash().put(hashKey, itemKey, hashmap);
        boolean isMember = redisTemplate.opsForHash().hasKey(hashKey, itemKey);
        System.out.println(isMember);
        Map<String, Object> map = (Map<String, Object>) redisTemplate.opsForHash().get(hashKey, itemKey);
//
        System.out.println(map.size());
        map.put(hashKey, (int)map.get("fake") + 1);
        redisTemplate.opsForHash().put(hashKey, itemKey, map);*/
//------------------------------------------------------------------------------------------
        boolean isMember = redisTemplate.opsForHash().hasKey(hashKey, itemKey);
        System.out.println(isMember);



        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {

                operations.multi();
                Map<String, Object> map = (Map<String, Object>) redisTemplate.opsForHash().get(hashKey, itemKey);
                System.out.println(map.get("fake"));

                return operations.exec();
            }
        });

    }

    @Test
    public void testGetWeek(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
        Calendar cld = Calendar.getInstance(Locale.CHINA);
        cld.setFirstDayOfWeek(Calendar.MONDAY);//以周一为首日
        cld.setTimeInMillis(System.currentTimeMillis());//当前时间

        cld.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);//周一
        System.out.println(df.format(cld.getTime()));

        cld.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);//周日
        System.out.println(df.format(cld.getTime()));
    }


}

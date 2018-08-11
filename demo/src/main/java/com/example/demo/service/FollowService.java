package com.example.demo.service;

import com.example.demo.util.JedisAdapter;
import com.example.demo.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class FollowService {
    @Autowired
    JedisAdapter jedisAdapter;
    public boolean follow(int userId,int entityType,int entityId){//关注一个实体，可以是问题,可以是一个人，userId是当前登录用户，entityType可以是user可以是question，entityid对应关注的人或者问题的ID
        String followerKey=RedisKeyUtil.getFollowerKey(entityType,entityId);//获取当前用户userId的粉丝列表的key//？？有点点逻辑不通，记得理一遍逻辑，还有jedisAdapter后面三个方法
        String followeeKey=RedisKeyUtil.getFolloweeKey(userId,entityType);//获取当前用户userId的关注列表的key
        Date date=new Date();
        Jedis jedis =jedisAdapter.getJedis();//获取jedis连接池的资源
        Transaction tx=jedisAdapter.multi(jedis);//开启jedis事务
        tx.zadd(followerKey,date.getTime(),String.valueOf(userId));//关注的人粉丝里面把关注人（即当前用户）加上
        tx.zadd(followeeKey,date.getTime(),String.valueOf(entityId));//当前用户的关注列表加上关注的人
        List<Object> ret=jedisAdapter.exec(tx,jedis);// 执行事务队列内命令，也就是上面的zadd方法
        return ret.size()==2&&(Long)ret.get(0)>0&&(Long)ret.get(1)>0;
    }
    public boolean unfollow(int userId,int entityType,int entityId){
        String followerKey=RedisKeyUtil.getFollowerKey(entityType,entityId);
        String followeeKey=RedisKeyUtil.getFolloweeKey(userId,entityType);
        Date date=new Date();
        Jedis jedis =jedisAdapter.getJedis();
        Transaction tx=jedisAdapter.multi(jedis);
        tx.zrem(followerKey,String.valueOf(userId));
        tx.zrem(followeeKey,String.valueOf(entityId));
        List<Object> ret=jedisAdapter.exec(tx,jedis);
        return ret.size()==2&&(Long)ret.get(0)>0&&(Long)ret.get(1)>0;
    }
    public List<Integer> getIdsFromSet(Set<String> idset){//把set<String> 转成List<Interger>
        List<Integer>ids=new ArrayList<>();
        for(String s:idset){
            ids.add(Integer.parseInt(s));
        }
        return ids;
    }
    //获取所有粉丝或者问题的粉丝的ID，根据entityType决定
    public List<Integer> getFollowers(int entityType,int entityId,int count){
        String followerKey=RedisKeyUtil.getFollowerKey(entityType,entityId);
        return getIdsFromSet(jedisAdapter.zrevrange(followerKey,0,count));
    }
    public List<Integer> getFollowers(int entityType,int entityId,int offset,int count){//获取粉丝，分页
        String followerKey=RedisKeyUtil.getFollowerKey(entityType,entityId);
        return getIdsFromSet(jedisAdapter.zrevrange(followerKey,offset,offset+count));
    }
    //获取关注的用户或者关注的问题的ID，根据entityType决定
    public List<Integer> getFollowees(int userId,int entityType,int count){
        String followeeKey=RedisKeyUtil.getFolloweeKey(userId,entityType);
        return getIdsFromSet(jedisAdapter.zrevrange(followeeKey,0,count));
    }
    public List<Integer> getFollowees(int userId,int entityType,int offset,int count){//获取关注者，分页
        String followeeKey=RedisKeyUtil.getFolloweeKey(userId,entityType);
        return getIdsFromSet(jedisAdapter.zrevrange(followeeKey,offset,offset+count));
    }
    //获取所有粉丝的数量或者问题的粉丝量
    public long getFollowerCount(int entityType,int entityId){
        String followerKey=RedisKeyUtil.getFollowerKey(entityType,entityId);
        return jedisAdapter.zcard(followerKey);
    }
    //获取所有关注的用户数量或者关注问题的数量
    public long getFolloweeCount(int userId,int entityType){
        String followeeKey=RedisKeyUtil.getFolloweeKey(userId,entityType);
        return jedisAdapter.zcard(followeeKey);
    }
    //判断userId是否关注该用户或者该问题
    public boolean isFollower(int userId,int entityType,int entityId){
        String followerKey=RedisKeyUtil.getFollowerKey(entityType,entityId);
        return jedisAdapter.zscore(followerKey,String.valueOf(userId))!=null;
    }
}

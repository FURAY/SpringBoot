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
    public boolean follow(int userId,int entityType,int entityId){//关注一个实体，可以是问题,可以是一个人
        String followerKey=RedisKeyUtil.getFollowerKey(entityType,entityId);//？？有点点逻辑不通，记得理一遍逻辑，还有jedisAdapter后面三个方法
        String followeeKey=RedisKeyUtil.getFolloweeKey(userId,entityType);
        Date date=new Date();
        Jedis jedis =jedisAdapter.getJedis();
        Transaction tx=jedisAdapter.multi(jedis);
        tx.zadd(followerKey,date.getTime(),String.valueOf(userId));//关注的人粉丝里面把关注人加上
        tx.zadd(followeeKey,date.getTime(),String.valueOf(entityId));//自己关注列表加上关注的人
        List<Object> ret=jedisAdapter.exec(tx,jedis);
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
    public List<Integer> getFollowers(int entityType,int entityId,int count){//获取粉丝
        String followerKey=RedisKeyUtil.getFollowerKey(entityType,entityId);
        return getIdsFromSet(jedisAdapter.zrevrange(followerKey,0,count));
    }
    public List<Integer> getFollowers(int entityType,int entityId,int offset,int count){//获取粉丝，分页
        String followerKey=RedisKeyUtil.getFollowerKey(entityType,entityId);
        return getIdsFromSet(jedisAdapter.zrevrange(followerKey,offset,offset+count));
    }

    public List<Integer> getFollowees(int userId,int entityType,int count){//获取关注者
        String followeeKey=RedisKeyUtil.getFolloweeKey(userId,entityType);
        return getIdsFromSet(jedisAdapter.zrevrange(followeeKey,0,count));
    }
    public List<Integer> getFollowees(int userId,int entityType,int offset,int count){//获取关注者，分页
        String followeeKey=RedisKeyUtil.getFolloweeKey(userId,entityType);
        return getIdsFromSet(jedisAdapter.zrevrange(followeeKey,offset,offset+count));
    }
    public long getFollowerCount(int entityType,int entityId){
        String followerKey=RedisKeyUtil.getFollowerKey(entityType,entityId);
        return jedisAdapter.zcard(followerKey);
    }
    public long getFolloweeCount(int userId,int entityType){
        String followeeKey=RedisKeyUtil.getFolloweeKey(userId,entityType);
        return jedisAdapter.zcard(followeeKey);
    }
    public boolean isFollower(int userId,int entityType,int entityId){
        String followerKey=RedisKeyUtil.getFollowerKey(entityType,entityId);
        return jedisAdapter.zscore(followerKey,String.valueOf(userId))!=null;
    }
}

package com.example.demo.service;

import com.example.demo.util.JedisAdapter;
import com.example.demo.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

@Service
public class LikeService {
    @Autowired
    JedisAdapter jedisAdapter;

    public int getLikeStatus(int useId,int entityType,int entityId){//返回特定用户赞的状态,看他是否是已赞或者其他状态好现实在页面上
        String likeKey=RedisKeyUtil.getLikeKey(entityType,entityId);
        if (jedisAdapter.sismember(likeKey,String.valueOf(useId))){
            return 1;//如果喜欢返回1
        }
        String dislikeKey=RedisKeyUtil.getDisLikeKey(entityType,entityId);
        return jedisAdapter.sismember(dislikeKey,String.valueOf(useId))?-1:0;//不喜欢返回-1,0代表即没有不喜欢也没后喜欢
    }
    public long getLikeCount(int entityType,int entityId){//返回评论有多少个喜欢的赞
        String likeKey=RedisKeyUtil.getLikeKey(entityType,entityId);
        return jedisAdapter.scard(likeKey);
    }

    public long like(int useId,int entityType,int entityId){//entityType,entityId 是评论的
        //喜欢的赞，增加
        String likeKey=RedisKeyUtil.getLikeKey(entityType,entityId);
        jedisAdapter.sadd(likeKey,String.valueOf(useId));
        //不喜欢的赞，删除
        String dislikeKey=RedisKeyUtil.getDisLikeKey(entityType,entityId);
        jedisAdapter.srem(dislikeKey,String.valueOf(useId));
        //返回有多少个喜欢赞
        return jedisAdapter.scard(likeKey);//先把like+1，然后返回like
    }

    public long dislike(int useId,int entityType,int entityId){//entityType,entityId 是评论的
        //不喜欢的赞，增加
        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityType, entityId);
        jedisAdapter.sadd(disLikeKey,String.valueOf(useId));



        //喜欢的赞，删除删除
        String likeKey=RedisKeyUtil.getLikeKey(entityType,entityId);
        jedisAdapter.srem(likeKey,String.valueOf(useId));
        //返回有多少个喜欢赞
        return jedisAdapter.scard(likeKey);//先把like-1，然后返回like
    }
}

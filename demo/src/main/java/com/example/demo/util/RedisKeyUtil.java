package com.example.demo.util;

public class RedisKeyUtil {//为了保证redis 的key不重复，给key加前缀不同业务不同的前缀
    private static String SPLIT=":";
    private static String BIZ_LIKE="LIKE";//喜欢的业务
    private static String BIZ_DISLIKE="DISLIKE";//不喜欢的业务
    private static String BIZ_EVENTQUEUE="EVENT_QUEUE";//事件的业务
    private static String BIZ_FOLLOWER="FOLLOWER";//粉丝
    private static String BIZ_FOLLOWEE="FOLLOWEE";//关注对象

    public static String getLikeKey(int entityType,int entityId){
        return BIZ_LIKE+SPLIT+String.valueOf(entityType)+SPLIT+String.valueOf(entityId);
    }
    public static String getDisLikeKey(int entityType,int entityId){
        return BIZ_DISLIKE+SPLIT+String.valueOf(entityType)+SPLIT+String.valueOf(entityId);
    }
    public static String getEventQueueKey(){
        return BIZ_EVENTQUEUE;
    }

    public static String getFollowerKey(int entityType,int entityId){//某一个实体所有粉丝的key，参数保证了唯一性
        return BIZ_FOLLOWER+SPLIT+String.valueOf(entityType)+SPLIT+String.valueOf(entityId);
    }

    public static String getFolloweeKey(int entityType,int userId){//某一个用户关注某一类实体的key
        return BIZ_FOLLOWEE+SPLIT+String.valueOf(userId)+SPLIT+String.valueOf(entityType);
    }
}


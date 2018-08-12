package com.example.demo.async.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.async.EventHandler;
import com.example.demo.async.EventModel;
import com.example.demo.async.EventType;
import com.example.demo.model.*;
import com.example.demo.service.*;
import com.example.demo.util.JedisAdapter;
import com.example.demo.util.RedisKeyUtil;
import com.example.demo.util.WendaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class FeedHandler implements EventHandler {
    @Autowired
    QuestionService questionService;
    @Autowired
    MessageService messageService;
    @Autowired
    UserService userService;
    @Autowired
    FeedService feedService;
    @Autowired
    FollowService followService;
    @Autowired
    JedisAdapter jedisAdapter;
    private String buildFeedData(EventModel model){
        Map<String,String> map=new HashMap<String,String>();
        User actor=userService.getUser(model.getActorId());
        if (actor==null){
            return null;
        }
        map.put("userId",String.valueOf(actor.getId()));
        map.put("userHead",actor.getHeadUrl());
        map.put("userName",actor.getName());

        if (model.getType()== EventType.COMMENT||(model.getType()==EventType.FOLLOW&&model.getEntityType()==EntityType.ENTITY_QUESTION)){//EntityType.ENTITY_COMMENT？？
            Question q=questionService.selectById(model.getEntityId());
            if (q==null)return null;
            map.put("questionId",String.valueOf(q.getId()));
            map.put("questionTitle",q.getTitle());
            return JSONObject.toJSONString(map);
        }
        return null;
    }
    @Override
    public void doHandler(EventModel model) {
        //拉
        Feed feed=new Feed();
        feed.setCreatedDate(new Date());
        feed.setUserId(model.getActorId());
        feed.setType(model.getType().getValue());
        //feed.setData(model.getExts("userId"));？？和LikeHandler区别
        feed.setData(buildFeedData(model));
        if (feed.getData()==null)return;
        feedService.addFeed(feed);

        //给粉丝推
        //这里对于粉丝数量大的，可以分段取，到时候再用类似addall方法加入
        List<Integer> followers=followService.getFollowers(EntityType.ENTITY_USER,model.getActorId(),Integer.MAX_VALUE);
        followers.add(0);//加入系统0
        for(int follower:followers){
            System.out.println(follower+":follower");
            String timelineKey=RedisKeyUtil.getTimelineKey(follower);

            jedisAdapter.lpush(timelineKey,String.valueOf(feed.getId()));

        }
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(new EventType[]{EventType.FOLLOW,EventType.COMMENT});//是关注FOLLOW和Commnet事件
        //Arrays.asList将一个数组转化为一个List对象，这个方法会返回一个ArrayList类型的对象
    }
}

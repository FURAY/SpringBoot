package com.example.demo.controller;

import com.example.demo.model.EntityType;
import com.example.demo.model.Feed;
import com.example.demo.model.HostHolder;
import com.example.demo.service.FeedService;
import com.example.demo.service.FollowService;
import com.example.demo.util.JedisAdapter;
import com.example.demo.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class FeedController {
    @Autowired
    FeedService feedService;
    @Autowired
    FollowService followService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    JedisAdapter jedisAdapter;
    @RequestMapping(path = {"/pullfeeds"},method = {RequestMethod.GET})
    private String getPullFeeds(Model model){
        int localUserId=hostHolder.getUser()==null?0:hostHolder.getUser().getId();

        List<Integer> followees=new ArrayList<Integer>();
        if (localUserId!=0){
         followees=followService.getFollowees(localUserId,EntityType.ENTITY_USER,Integer.MAX_VALUE);
        }
        List<Feed> feeds =feedService.getUserFeed(Integer.MAX_VALUE,followees,10);
        model.addAttribute("feeds",feeds);
        return "feeds";
    }

    @RequestMapping(path = {"/pushfeeds"},method = {RequestMethod.GET})
    private String getPushFeeds(Model model){
        int localUserId=hostHolder.getUser()==null?0:hostHolder.getUser().getId();

        List<String> feedIds=jedisAdapter.lrange(RedisKeyUtil.getTimelineKey(localUserId),0,10);
        List<Feed> feeds=new ArrayList<Feed>();
        for(String feedId:feedIds){
            Feed feed=feedService.getFeedById(Integer.parseInt(feedId));
            if (feed==null)continue;
            feeds.add(feed);
        }
        model.addAttribute("feeds",feeds);
        return "feeds";
    }
}

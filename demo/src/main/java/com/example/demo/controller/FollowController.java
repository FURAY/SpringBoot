package com.example.demo.controller;

import com.example.demo.async.EventModel;
import com.example.demo.async.EventProducer;
import com.example.demo.async.EventType;
import com.example.demo.model.*;
import com.example.demo.service.CommentService;
import com.example.demo.service.FollowService;
import com.example.demo.service.QuestionService;
import com.example.demo.service.UserService;
import com.example.demo.util.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class FollowController {//这里是评论系统，因为评论可以分为给问题评论和给评论回复的评论，具体
    private static final Logger logger = LoggerFactory.getLogger(FollowController.class);
    @Autowired
    HostHolder hostHolder;
    @Autowired
    CommentService commentService;
    @Autowired
    QuestionService questionService;
    @Autowired
    FollowService followService;
    @Autowired
    UserService userService;
    @Autowired
    EventProducer eventProducer;
    @RequestMapping(path = {"/followUser"},method = {RequestMethod.POST})
    @ResponseBody
    public String followUser(@RequestParam("userId") int userId){
        if(hostHolder.getUser()==null){
            return WendaUtil.getJSONString(999);
        }
        boolean ret=followService.follow(hostHolder.getUser().getId(),EntityType.ENTITY_USER,userId);
        eventProducer.fireEvent(new EventModel(EventType.FOLLOW)
                .setActorId(hostHolder.getUser().getId())
                .setEntityId(userId)
                .setEntityType(EntityType.ENTITY_USER)
                .setEntityOwnerId(userId));
        //如果关注成功，返回0和当前用户的关注数量（即用于更新关注数）
        return WendaUtil.getJSONString(ret ?0 :1,String.valueOf(followService.getFolloweeCount(hostHolder.getUser().getId(),EntityType.ENTITY_USER)));
    }

    @RequestMapping(path = {"/unfollowUser"},method = {RequestMethod.POST})
    @ResponseBody
    public String unfollowUser(@RequestParam("userId") int userId){
        if(hostHolder.getUser()==null){
            return WendaUtil.getJSONString(999);
        }
        boolean ret=followService.unfollow(hostHolder.getUser().getId(),EntityType.ENTITY_USER,userId);
        eventProducer.fireEvent(new EventModel(EventType.FOLLOW)
                .setActorId(hostHolder.getUser().getId())
                .setEntityId(userId)
                .setEntityType(EntityType.ENTITY_USER)
                .setEntityOwnerId(userId));
        return WendaUtil.getJSONString(ret ?0 :1,String.valueOf(followService.getFolloweeCount(hostHolder.getUser().getId(),EntityType.ENTITY_USER)));
    }

    @RequestMapping(path = {"/followQuestion"},method = {RequestMethod.POST})
    @ResponseBody
    public String followQuestion(@RequestParam("questionId") int questionId){
        if(hostHolder.getUser()==null){
            return WendaUtil.getJSONString(999);
        }

        Question q=questionService.selectById(questionId);
        if (q==null){
            return WendaUtil.getJSONString(1,"问题不存在");
        }

        boolean ret=followService.follow(hostHolder.getUser().getId(),EntityType.ENTITY_QUESTION,questionId);
        eventProducer.fireEvent(new EventModel(EventType.FOLLOW)
                .setActorId(hostHolder.getUser().getId())
                .setEntityId(questionId)
                .setEntityType(EntityType.ENTITY_QUESTION)
                .setEntityOwnerId(q.getUserId()));
        Map<String,Object> info=new HashMap<String,Object>();
        info.put("headUrl",hostHolder.getUser().getHeadUrl());
        info.put("userId",hostHolder.getUser().getId());
        info.put("name",hostHolder.getUser().getName());
        info.put("count",followService.getFollowerCount(EntityType.ENTITY_QUESTION,questionId));
        return WendaUtil.getJSONString(ret ? 0 :1,info);
    }
    @RequestMapping(path = {"/unfollowQuestion"},method = {RequestMethod.POST})
    @ResponseBody
    public String unfollowQuestion(@RequestParam("questionId") int questionId){
        if(hostHolder.getUser()==null){
            return WendaUtil.getJSONString(999);
        }
        Question q=questionService.selectById(questionId);
        if (q==null){
            return WendaUtil.getJSONString(1,"问题不存在");
        }
        boolean ret=followService.unfollow(hostHolder.getUser().getId(),EntityType.ENTITY_QUESTION,questionId);
        eventProducer.fireEvent(new EventModel(EventType.FOLLOW)
                .setActorId(hostHolder.getUser().getId())
                .setEntityId(questionId)
                .setEntityType(EntityType.ENTITY_QUESTION)
                .setEntityOwnerId(q.getUserId()));
        Map<String,Object> info=new HashMap<String,Object>();
        info.put("headUrl",hostHolder.getUser().getHeadUrl());
        info.put("userId",hostHolder.getUser().getId());
        info.put("name",hostHolder.getUser().getName());
        info.put("count",followService.getFollowerCount(EntityType.ENTITY_QUESTION,questionId));
        return WendaUtil.getJSONString(ret ? 0 :1,info);
    }

    @RequestMapping(path = {"/user/{uid}/followers"}, method = {RequestMethod.GET})//粉丝页面
    public String followers(Model model, @PathVariable("uid") int userId) {
        List<Integer> followerIds = followService.getFollowers(EntityType.ENTITY_USER, userId, 0, 10);//获取所有粉丝或者问题的ID
        if (hostHolder.getUser() != null) {
            model.addAttribute("followers", getUsersInfo(hostHolder.getUser().getId(), followerIds));
        } else {
            model.addAttribute("followers", getUsersInfo(0, followerIds));
        }
        model.addAttribute("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, userId));
        model.addAttribute("curUser", userService.getUser(userId));
        return "followers";
    }
    @RequestMapping(path = {"/user/{uid}/followees"},method = {RequestMethod.GET})
    public String followees(Model model,@PathVariable("uid") int userId) {
        List<Integer> followeeIds = followService.getFollowees(userId, EntityType.ENTITY_USER, 0, 10);
        if (hostHolder.getUser() != null) {
            model.addAttribute("followees", getUsersInfo(hostHolder.getUser().getId(), followeeIds));
        } else {
            model.addAttribute("followees", getUsersInfo(0, followeeIds));
        }
        model.addAttribute("followeeCount", followService.getFolloweeCount(userId, EntityType.ENTITY_USER));
        model.addAttribute("curUser", userService.getUser(userId));
        return "followees";
    }
    private List<ViewObject> getUsersInfo(int localUserId,List<Integer> userIds){//根据获取的所有粉丝或者问题的ID，获取它实体各种信息，存进ViewObject里
        List<ViewObject> userInfos = new ArrayList<ViewObject>();
        for (Integer uid : userIds) {
            User user = userService.getUser(uid);
            if (user == null) {
                continue;
            }
            ViewObject vo = new ViewObject();
            vo.set("user", user);
            vo.set("commentCount", commentService.getUserCommentCount(uid));
            vo.set("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, uid));
            vo.set("followeeCount", followService.getFolloweeCount(uid, EntityType.ENTITY_USER));
            if (localUserId != 0) {
                vo.set("followed", followService.isFollower(localUserId, EntityType.ENTITY_USER, uid));
            } else {
                vo.set("followed", false);
            }
            System.out.println(vo.get("followed"));
            userInfos.add(vo);
        }
        return userInfos;
    }

}

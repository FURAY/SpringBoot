package com.example.demo.controller;

import com.example.demo.async.EventModel;
import com.example.demo.async.EventProducer;
import com.example.demo.async.EventType;
import com.example.demo.model.Comment;
import com.example.demo.model.EntityType;
import com.example.demo.model.HostHolder;
import com.example.demo.service.CommentService;
import com.example.demo.service.LikeService;
import com.example.demo.util.WendaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class LikeController {
    @Autowired
    LikeService likeService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    EventProducer eventProducer;
    @Autowired
    CommentService commentService;

    @RequestMapping(path = {"/like"},method = {RequestMethod.POST})
    @ResponseBody
    public String like(@RequestParam("commentId") int commentId){

        if(hostHolder.getUser()==null){
            return WendaUtil.getJSONString(999);
        }

        Comment comment=commentService.getCommentById(commentId);
        eventProducer.fireEvent(new EventModel(EventType.LIKE)
                .setActorId(hostHolder.getUser().getId())
                .setEntityId(commentId)
                .setEntityType(EntityType.ENTITY_COMMENT)
                .setExts("questionId",String.valueOf(comment.getEntityId()))
                .setEntityOwnerId(comment.getUserId()));

        long likeCount=likeService.like(hostHolder.getUser().getId(),EntityType.ENTITY_COMMENT,commentId);
       // System.out.println(String.valueOf(likeCount)+" like "+commentId);
        return WendaUtil.getJSONString(0,String.valueOf(likeCount));
    }
    @RequestMapping(path = {"/dislike"},method = {RequestMethod.POST})
    @ResponseBody
    public String dislike(@RequestParam("commentId") int commentId){
        if(hostHolder.getUser()==null){
            return WendaUtil.getJSONString(999);
        }
        long likeCount=likeService.dislike(hostHolder.getUser().getId(),EntityType.ENTITY_COMMENT,commentId);
       // System.out.println(String.valueOf(likeCount)+"dislike");
        return WendaUtil.getJSONString(0,String.valueOf(likeCount));
    }
}

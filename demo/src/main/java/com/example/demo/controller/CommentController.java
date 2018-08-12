package com.example.demo.controller;

import com.example.demo.async.EventModel;
import com.example.demo.async.EventProducer;
import com.example.demo.async.EventType;
import com.example.demo.model.Comment;
import com.example.demo.model.EntityType;
import com.example.demo.model.HostHolder;
import com.example.demo.service.CommentService;
import com.example.demo.service.QuestionService;
import com.example.demo.util.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.swing.text.html.parser.Entity;
import java.util.Date;

@Controller
public class CommentController {//这里是评论系统，因为评论可以分为给问题评论和给评论回复的评论，具体
    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);
    @Autowired
    HostHolder hostHolder;
    @Autowired
    CommentService commentService;
    @Autowired
    QuestionService questionService;
    @Autowired
    EventProducer eventProducer;
    @RequestMapping(path = {"/addComment"},method = {RequestMethod.POST})
    public String addComment(@RequestParam("questionId") int questionId,
                             @RequestParam("content")String content){
        try{
            Comment comment=new Comment();
            comment.setContent(content);
            if(hostHolder.getUser()!=null){
                comment.setUserId(hostHolder.getUser().getId());
            }else{
                //return "redirect:/relogin"//跳转去登录
                comment.setUserId(WendaUtil.ANONYMOUS_USERID);
            }
            comment.setCreatedDate(new Date());
            comment.setEntityType(EntityType.ENTITY_QUESTION);
            comment.setEntityId(questionId);
            commentService.addComment(comment);

            int count=commentService.getCommentCount(comment.getEntityId(),comment.getEntityType());
            System.out.println(count+":count");
            eventProducer.fireEvent(new EventModel(EventType.COMMENT).setActorId(comment.getUserId()).setEntityId(questionId));
            questionService.updateCommentCount(count,comment.getEntityId());
        }catch (Exception e){
            logger.error("添加评论失败"+e.getMessage());
        }
        return "redirect:/question/"+questionId;

    }

}

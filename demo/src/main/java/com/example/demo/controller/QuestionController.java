package com.example.demo.controller;

import com.example.demo.model.*;
import com.example.demo.service.*;
import com.example.demo.util.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class QuestionController {
    private static final Logger logger = LoggerFactory.getLogger(QuestionController.class);

    @Autowired
    QuestionService questionService;
    @Autowired
    UserService userService;
    @Autowired
    CommentService commentService;
    @Autowired
    LikeService likeService;
    @Autowired
    HostHolder hostHolder;

    @Autowired
    FollowService followService;
    //path和value都可以 ，有什么区别？
    @RequestMapping(path = "/question/add" ,method = {RequestMethod.POST})
    @ResponseBody//要返回一个json char，一个格式规范
    public String addQuestion(@RequestParam("title") String title,
                              @RequestParam("content") String content){
        try {
            Question question=new Question();
            question.setContent(content);
            question.setTitle(title);
            question.setCreatedDate(new Date());
            question.setCommentCount(0);
            if(hostHolder.getUser()==null){
                //question.setUserId(WendaUtil.ANONYMOUS_USERID);
                return WendaUtil.getJSONString(999);//999代表要登录，会跳转到登录界面
            }
            else{
                question.setUserId(hostHolder.getUser().getId());
            }
            if(questionService.addQuestion(question)>0){//成功返回的是UserID，一定大于0说明他添加成功
                return WendaUtil.getJSONString(0);//0代表成功
            }
        }catch (Exception e){
            logger.error("增加题目失败"+e.getMessage());
        }
        return WendaUtil.getJSONString(1,"增加失败");//1代表出错
    }

    @RequestMapping(path={"/question/{questionID}"},method ={RequestMethod.GET})
    public String questionDetail(Model model,
                                 @PathVariable("questionID") int questionID){
        Question question=questionService.selectById(questionID);
        model.addAttribute("question",question);
        model.addAttribute("user",userService.getUser(question.getUserId()));
        //上面是放入quesion的
        //下面是放入comment的
        List<Comment> commentList=commentService.getCommentByEntity(questionID,EntityType.ENTITY_QUESTION);
        List<ViewObject> comments=new ArrayList<ViewObject>();
        for (Comment comment:commentList){
            ViewObject vo=new ViewObject();
            vo.set("comment",comment);
            if (hostHolder.getUser() == null) {
                vo.set("liked", 0);
            } else {
                vo.set("liked", likeService.getLikeStatus(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT, comment.getId()));
            }
            vo.set("likeCount",likeService.getLikeCount(EntityType.ENTITY_COMMENT,comment.getId()));
            vo.set("user",userService.getUser(comment.getUserId()));
            comments.add(vo);
        }
        model.addAttribute("commets",comments);
        List<ViewObject> followUsers = new ArrayList<ViewObject>();
        // 获取关注的用户信息
        List<Integer> users = followService.getFollowers(EntityType.ENTITY_QUESTION, questionID, 20);
        for (Integer userId : users) {
            ViewObject vo = new ViewObject();
            User u = userService.getUser(userId);
            if (u == null) {
                continue;
            }
            vo.set("name", u.getName());
            vo.set("headUrl", u.getHeadUrl());
            vo.set("id", u.getId());
            followUsers.add(vo);
        }
        model.addAttribute("followUsers", followUsers);
        if (hostHolder.getUser() != null) {
            model.addAttribute("followed", followService.isFollower(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION, questionID));
        } else {
            model.addAttribute("followed", false);
        }
        return "detail";
    }
}

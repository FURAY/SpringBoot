package com.example.demo.controller;

import com.example.demo.model.*;
import com.example.demo.service.CommentService;
import com.example.demo.service.FollowService;
import com.example.demo.service.QuestionService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class IndexController {
    @Autowired
    UserService userService;
    @Autowired
    QuestionService questionService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    CommentService commentService;
    @Autowired
    FollowService followService;
   /* @RequestMapping(path = {"/","index"},method={RequestMethod.GET})
    public String method(Model model){
        List<User> items=new ArrayList<User>();
        for(int i=1;i<12;i++){
            User user=userService.getUser(i);
            System.out.println(user.getId());
            items.add(user);
        }
        System.out.println(items.get(3).getName());
        model.addAttribute("items",items);
        return "index";
    }*/




    private List<ViewObject> getQuestions(int userId, int offset, int limit) {
        List<Question> questionList = questionService.getLatestQuestions(userId, offset, limit);
        List<ViewObject> vos = new ArrayList<>();
        for (Question question : questionList) {
            ViewObject vo = new ViewObject();
            vo.set("question", question);
            //vo.set("user",hostHolder.getUser());//通过hostHolder上下文来读取当前用户是谁
            vo.set("followCount", followService.getFollowerCount(EntityType.ENTITY_QUESTION, question.getId()));//首页可以看到该问题有多少人关注
            vo.set("user", userService.getUser(question.getUserId()));
            vos.add(vo);
        }
        return vos;
    }

    @RequestMapping(value = {"/", "/index"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String index(Model model) {
        model.addAttribute("vos", getQuestions(0, 0, 10));
        return "index";
    }

    /*@RequestMapping(path = {"/user/{userId}"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String userIndex(Model model,
                            @PathVariable("userId") int userId) {
        model.addAttribute("vos", getQuestions(userId, 0, 10));
        return "index";
    }*/
    @RequestMapping(path = {"/user/{userId}"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String userIndex(Model model, @PathVariable("userId") int userId) {
        model.addAttribute("vos", getQuestions(userId, 0, 10));

        User user = userService.getUser(userId);
        ViewObject vo = new ViewObject();
        vo.set("user", user);
        vo.set("commentCount", commentService.getUserCommentCount(userId));//有多少个评论
        vo.set("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, userId));//有多少个粉丝
        vo.set("followeeCount", followService.getFolloweeCount(userId, EntityType.ENTITY_USER));//关注多少人
        if (hostHolder.getUser() != null) {//当前登录用户和当前用户是否是关注关系
            vo.set("followed", followService.isFollower(hostHolder.getUser().getId(), EntityType.ENTITY_USER, userId));
        } else {
            vo.set("followed", false);
        }
        System.out.println(vo.get("followed")+":profile");
        model.addAttribute("profileUser", vo);
        return "profile";
    }
}

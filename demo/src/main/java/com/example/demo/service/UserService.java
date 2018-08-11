package com.example.demo.service;

import com.example.demo.dao.LoginTicketDAO;
import com.example.demo.dao.UserDAO;
import com.example.demo.model.LoginTicket;
import com.example.demo.model.User;

import com.example.demo.util.WendaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;


import java.util.*;

@Service
public class UserService {
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private LoginTicketDAO loginTicketDAO;

    public User getUser(int id){
        //System.out.println(id+"???");
        return userDAO.selectById(id);
    }
    public User selectByName(String name){
        return userDAO.selectByName(name);
    }
    public Map<String,String> register(String username, String password){//Map是要返回各种各样字段，用户被已被注册，密码强度不够等
        Map<String,String> map=new HashMap<String, String>() ;
        //System.out.println("22222"+username);
        if(StringUtils.isEmpty(username)){//StringUtils,ArrayUtils,判断密码是否为空等等，这些Utils帮助类常用的简单判断都已经封装好了
            map.put("msg","用户名为空");
            return map;
        }
        if(StringUtils.isEmpty(password)){
            map.put("msg","密码为空");
            return map;
        }
        User user=userDAO.selectByName(username);
        System.out.println(user);
        if(user!=null){
            map.put("msg","用户名已经被注册");
            return map;
        }
        user=new User();
        user.setName(username);
        user.setSalt(UUID.randomUUID().toString().substring(0,5));
        user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setPassword(WendaUtil.MD5(user.getSalt()+password));
        userDAO.addUser(user);
        //注册成功，自动登录跳转首页，给它下发个ticket
        String ticket=addLoginTicket(user.getId());
        map.put("ticket",ticket);
        return map;
    }
    public Map<String,Object> login(String username, String password){
        Map<String,Object> map=new HashMap<String, Object>() ;
        if(StringUtils.isEmpty(username)){
            map.put("msg","用户名为空");
            return map;
        }
        if(StringUtils.isEmpty(password)){
            map.put("msg","密码为空");
            return map;
        }
        User user=userDAO.selectByName(username);
        if(user==null){
            map.put("msg","用户名不存在");
            return map;
        }

        if(!WendaUtil.MD5(user.getSalt()+password).equals(user.getPassword())){
             map.put("msg","密码错误");
             return map;
        }
        //说明登录成功，给它一个ticket关联这个用户，是常见的登录状态做法

        String ticket=addLoginTicket(user.getId());
        map.put("ticket",ticket);
        map.put("userId",user.getId());
        return map;
    }
    public String addLoginTicket(int useid){
        LoginTicket loginTicket=new LoginTicket();

        loginTicket.setUserId(useid);

        Date now=new Date();
        now.setTime(3600*24*100+now.getTime());//设置过期的时间为现在过去的100天
        loginTicket.setExpired(now);
        loginTicket.setStatus(0);//0表示状态有效
        loginTicket.setTicket(UUID.randomUUID().toString().replaceAll("-",""));
     //   System.out.println(useid+"1");
        loginTicketDAO.addTicket(loginTicket);
        //System.out.println(loginTicket.getUserId()+"2");
        return loginTicket.getTicket();
    }
    public void logout(String ticket){
        loginTicketDAO.updateStatus(ticket,1);
    }
}

package com.example.demo.controller;

import com.example.demo.service.UserService;
import com.sun.deploy.net.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.StringUtils;


import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;


@Controller
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    @Autowired
    UserService userService;
    @RequestMapping(path = {"/reg/"}, method = {RequestMethod.POST})
    public String reg(Model model,
                       @RequestParam("username") String username,
                       @RequestParam("password") String password,
                      @RequestParam(value = "next",required = false)String next,
                       HttpServletResponse response) {
        try {
            Map<String,String> map=userService.register(username,password);
            /*if(map.containsKey("msg")){
                model.addAttribute("msg",map.get("msg"));
                return "login";
            }
            return "redirect:/";*/
            if(map.containsKey("ticket")){
                Cookie cookie=new Cookie("ticket",map.get("ticket"));//把ticket写进网页的ticket里，证明他是登录状态
                cookie.setPath("/");//不知道什么意思还没去查//同一应用服务器内共享cookie
                response.addCookie(cookie);
                if(!StringUtils.isEmpty(next)){
                    return "redirect:"+next;
                }
                return "redirect:/";
            }else {
                model.addAttribute("msg",map.get("msg"));
                return "login";
            }
        }catch (Exception e){
            logger.error("注册异常" + e.getMessage());
            return "login";
        }
    }

    @RequestMapping(path = {"/login/"}, method = {RequestMethod.POST})
    public String login(Model model,
                        @RequestParam("username") String username,
                        @RequestParam("password") String password,
                        @RequestParam(value = "next",required = false)String next,
                        @RequestParam(value = "remeber_me",defaultValue = "false") boolean remberme,
                        //这里的RequestParam的字段要和网页的input中name相同
                        HttpServletResponse response) {
        try {
            Map<String,Object> map=userService.login(username,password);
           // System.out.println("11111");
            if(map.containsKey("ticket")){
              //  System.out.println("22222");
                Cookie cookie=new Cookie("ticket",map.get("ticket").toString());//把ticket写进网页的ticket里，证明他是登录状态
                cookie.setPath("/");//不知道什么意思还没去查
                response.addCookie(cookie);
                if(!StringUtils.isEmpty(next)){
                    return "redirect:"+next;
                }
                return "redirect:/";
            }else {
                model.addAttribute("msg",map.get("msg"));
                return "login";
            }


        }catch (Exception e){
            logger.error("登录异常" + e.getMessage());
            return "login";
        }
    }

    @RequestMapping(path = {"/relogin/","/relogin"}, method = {RequestMethod.GET})
    public String Relogin(Model model,
                          @RequestParam(value = "next",required = false)String next) {
        //这里可以直接用RequestParam取到网页的参数
        model.addAttribute("next",next);
        return "login";
    }

    @RequestMapping(path = {"/logout/"}, method = {RequestMethod.GET})
    public String Logout(@CookieValue("ticket") String ticket) {
        userService.logout(ticket);
        return "redirect:/";
    }
}

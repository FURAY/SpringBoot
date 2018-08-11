package com.example.demo.controller;

import com.example.demo.model.HostHolder;
import com.example.demo.model.Message;
import com.example.demo.model.User;
import com.example.demo.model.ViewObject;
import com.example.demo.service.MessageService;
import com.example.demo.service.UserService;
import com.example.demo.util.WendaUtil;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.View;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class MessageController {
    @Autowired
    HostHolder hostHolder;
    @Autowired
    MessageService messageService;
    @Autowired
    UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);
    @RequestMapping(path = {"/msg/list"},method = {RequestMethod.GET})
    public String getConversationList(Model model){

        if (hostHolder.getUser()==null)
            return "redirect:/relogin";
            /*return WendaUtil.getJSONString(999,"未登录"); 因为不是@ResponseBody所以不需要返回json*/
        int localUserId=hostHolder.getUser().getId();

        try{
            List<Message> messageList=messageService.getConversationList(localUserId,0,10);
            List<ViewObject> conversations=new ArrayList<ViewObject>();
            for(Message message:messageList){
                ViewObject view=new ViewObject();
                view.set("message",message);
                int targetId=message.getFromId()==localUserId?message.getToId():message.getFromId();
                view.set("user",userService.getUser(targetId));
                System.out.println(messageService.getConvesationUnreadCount(localUserId, message.getConversationId())+" "+localUserId+" "+message.getConversationId());
                view.set("unread", messageService.getConvesationUnreadCount(localUserId, message.getConversationId()));

                conversations.add(view);
            }
            model.addAttribute("conversations",conversations);
        }catch (Exception e){
            logger.error("获取ConversationList详情失败"+e.getMessage());
        }
        return "letter";
    }
    @RequestMapping(path = {"/msg/detail"},method = {RequestMethod.GET})
    public String getConversationDetail(Model model,
                                        @RequestParam("conversationId") String conversationId){
        try{
            List<Message> messageList=messageService.getConversationDetail(conversationId,0,10);
            List<ViewObject> messages=new ArrayList<ViewObject>();
            for(Message message:messageList){
                ViewObject view=new ViewObject();
                view.set("message",message);
                view.set("user",userService.getUser(message.getFromId()));

                messages.add(view);
            }
            model.addAttribute("messages",messages);
        }catch (Exception e){
            logger.error("获取详情失败"+e.getMessage());
        }
        return "letterDetail";
    }

    @RequestMapping(path = "/msg/addMessage",method = {RequestMethod.POST})
    @ResponseBody
    public String addMessage(@RequestParam("toName") String toName,
                             @RequestParam("content") String content){
        try {
            if(hostHolder.getUser()==null){
                return WendaUtil.getJSONString(999);
            }
            User user=userService.selectByName(toName);
            if(user==null){
                return WendaUtil.getJSONString(1,"用户不存在");
            }
            Message message=new Message();
            message.setCreatedDate(new Date());
            message.setFromId(hostHolder.getUser().getId());
            message.setToId(user.getId());
            message.setContent(content);
            messageService.addMessage(message);

            return WendaUtil.getJSONString(0);
        }catch (Exception e){
            logger.error("发送消失失败"+e.getMessage());
            return WendaUtil.getJSONString(1,"发信失败");
        }
    }
}

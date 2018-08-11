package com.example.demo.async.handler;

import com.example.demo.async.EventHandler;
import com.example.demo.async.EventModel;
import com.example.demo.async.EventType;
import com.example.demo.model.EntityType;
import com.example.demo.model.Message;
import com.example.demo.model.User;
import com.example.demo.service.MessageService;
import com.example.demo.service.UserService;
import com.example.demo.util.WendaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class FollowHandler implements EventHandler {
    @Autowired
    MessageService messageService;
    @Autowired
    UserService userService;
    @Override
    public void doHandler(EventModel model) {
        Message message=new Message();
        message.setFromId(WendaUtil.SYSTEM_USERID);//系统站内信，所有用的是系统ID
        message.setToId(model.getEntityOwnerId());
        message.setCreatedDate(new Date());
        User user=userService.getUser(model.getActorId());
        if (model.getEntityType() == EntityType.ENTITY_QUESTION) {
            message.setContent("用户" + user.getName()
                    + "关注了你的问题,http://127.0.0.1:8080/question/" + model.getEntityId());
        } else if (model.getEntityType() == EntityType.ENTITY_USER) {
            message.setContent("用户" + user.getName()
                    + "关注了你,http://127.0.0.1:8080/user/" + model.getActorId());
        }

        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.FOLLOW);//是关注FOLLOW事件
        //Arrays.asList将一个数组转化为一个List对象，这个方法会返回一个ArrayList类型的对象
    }
}

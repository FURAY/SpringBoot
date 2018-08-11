package com.example.demo.async.handler;

import com.example.demo.async.EventHandler;
import com.example.demo.async.EventModel;
import com.example.demo.async.EventType;
import com.example.demo.model.Message;
import com.example.demo.model.User;
import com.example.demo.service.FeedService;
import com.example.demo.service.MessageService;
import com.example.demo.service.UserService;
import com.example.demo.util.WendaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class LikeHandler implements EventHandler {
    @Autowired
    MessageService messageService;
    @Autowired
    UserService userService;

    @Override
    public void doHandler(EventModel model) {
        Message message=new Message();
        message.setFromId(WendaUtil.SYSTEM_USERID);
        message.setToId(model.getEntityOwnerId());
        message.setCreatedDate(new Date());
        User user=userService.getUser(model.getActorId());
        message.setContent("用户"+user.getName()+"赞了你的评论,http://127.0.0.1:8080/question/"+model.getExts("questionId"));

        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LIKE);//是关注Like事件
        //Arrays.asList将一个数组转化为一个List对象，这个方法会返回一个ArrayList类型的对象
    }
}

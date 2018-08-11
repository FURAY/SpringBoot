package com.example.demo.service;

import com.example.demo.dao.MessageDAO;
import com.example.demo.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class MessageService {
    @Autowired
    MessageDAO messageDAO;

    @Autowired
    SensitiveService sensitiveService;

    public int addMessage(Message message){
        message.setContent(sensitiveService.filter(message.getContent()));
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        return messageDAO.addMessage(message)>0?message.getId():0;
    }
    public List<Message> getConversationDetail(String conversationId,int offest,int limit){
        return messageDAO.selectConversationDetail(conversationId,offest,limit);
    }
    public List<Message> getConversationList(int userId,int offest,int limit){
        return messageDAO.selectConversationList(userId,offest,limit);
    }

    public int getConvesationUnreadCount(int userId, String conversationId) {
        return messageDAO.getConvesationUnreadCount(userId, conversationId);
    }
}

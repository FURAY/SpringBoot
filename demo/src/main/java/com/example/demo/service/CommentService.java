package com.example.demo.service;

import com.example.demo.dao.CommentDAO;
import com.example.demo.dao.QuestionDAO;
import com.example.demo.model.Comment;
import com.example.demo.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class CommentService {
    @Autowired
    CommentDAO commentDAO;
    @Autowired
    SensitiveService sensitiveService;
    public int addComment(Comment comment){
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveService.filter(comment.getContent()));
        return commentDAO.addComment(comment)>0?comment.getId():0;
    }
    public int getCommentCount(int entityId,int entityType){
        return commentDAO.getCommentCount(entityId,entityType);
    }
    public List<Comment> getCommentByEntity(int entityId,int entityType){
        return commentDAO.selectCommentByEntity(entityId,entityType);
    }

    public boolean deleteComment(int commentId){
        return commentDAO.deleteComment(commentId,1)>0;
    }
    public Comment getCommentById(int id){
       return commentDAO.selectCommentById(id);
    }
    public int getUserCommentCount(int userId) {
        return commentDAO.getUserCommentCount(userId);
    }
}

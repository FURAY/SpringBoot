package com.example.demo.service;

import com.example.demo.dao.QuestionDAO;
import com.example.demo.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;
@Service
public class QuestionService {
    @Autowired
    QuestionDAO questionDAO;
    @Autowired
    SensitiveService sensitiveService;


    public int addQuestion(Question question){
        //过滤html文本过滤，如question中Content有<srcpts>alter<'hi'></srcpts>，存进来时就会去过滤这个，把<,>等一些html特有的语法转义掉，就不会出现提交js文件，如果不做的话，有些黑客可以写一写js脚本去获取你的cookie这样就会泄露了
        question.setContent(HtmlUtils.htmlEscape(question.getContent()));
        question.setTitle(HtmlUtils.htmlEscape(question.getTitle()));
        //要加个敏感词过滤
        question.setTitle(sensitiveService.filter(question.getTitle()));
        question.setContent(sensitiveService.filter(question.getContent()));


        return questionDAO.addQuestion(question)>0?question.getUserId():0;
    }

    public List<Question> getLatestQuestions(int userId, int offset, int limit) {
        return questionDAO.selectLatestQuestions(userId, offset, limit);
    }
    public Question selectById(int questionID){
        return questionDAO.selectById(questionID);
    }
    public int updateCommentCount(int commentCount,int questionId){
        return questionDAO.updateCommentCount(commentCount,questionId);
    }
}

package com.example.demo.interceptor;

import com.example.demo.dao.LoginTicketDAO;
import com.example.demo.dao.UserDAO;
import com.example.demo.model.HostHolder;
import com.example.demo.model.LoginTicket;
import com.example.demo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class PassportInterceptor implements HandlerInterceptor {
    @Autowired
    LoginTicketDAO loginTicketDAO;
    @Autowired
    UserDAO userDAO;
    @Autowired
    HostHolder hostHolder;
//在请求开始之前调用，如果返回false后面整个请求都结束了
@Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        String ticket=null;
        if(httpServletRequest.getCookies()!=null){
            for(Cookie cookie:httpServletRequest.getCookies()){
                if(cookie.getName().equals("ticket")){
                    ticket=cookie.getValue();
                    break;
                }
            }
        }
        if(ticket!=null){
            LoginTicket loginTicket=loginTicketDAO.selectByTicket(ticket);
            if(loginTicket==null||loginTicket.getExpired().before(new Date())||loginTicket.getStatus()!=0){
                return true;
            }
            //System.out.println(loginTicket.getUserId()+"???"+loginTicket.getTicket()+" "+loginTicket.getId()+" "+loginTicket.getExpired());
            User user=userDAO.selectById(loginTicket.getUserId());
            //System.out.println(user.getName()+"hahah");
            hostHolder.setUser(user);//用一个上下文的功能，在请求开始之前放进这个user记录下来，这样后面整个过程都可以调用这个user
        }
        return true;
    }
//handler处理完，渲染之前，再回调
    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        if(modelAndView!=null){//model就是controller中那个model，view就是html静态模板
            modelAndView.addObject("user",hostHolder.getUser());//这个就相当于model.addAttribute("user",hostHolder.getUser())
        }
        //这里整个就是，当要访问html时，发现如果modelandView为空，把user放进去，方便html调用
    }
//整个都渲染完了再回调
    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        //System.out.println("heheh");
        hostHolder.clear();//整个过程调用完结束后，清空这个user
    }
}

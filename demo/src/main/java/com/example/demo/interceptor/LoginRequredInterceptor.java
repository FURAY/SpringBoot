package com.example.demo.interceptor;

import com.example.demo.model.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
@Component
public class LoginRequredInterceptor implements HandlerInterceptor {
    @Autowired
    HostHolder hostHolder;
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        if(hostHolder.getUser()==null){//当你打开这个页面时候，来判断一下你是否登录，未登录直接跳转到登录页面，并且加上当前的页面地址（getRequestURI），这样登录后可以跳转回来
            httpServletResponse.sendRedirect("/relogin?next="+httpServletRequest.getRequestURI());

        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}

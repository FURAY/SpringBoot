package com.example.demo.configuration;

import com.example.demo.interceptor.LoginRequredInterceptor;
import com.example.demo.interceptor.PassportInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Component
public class DemoWebConfiguration extends WebMvcConfigurerAdapter {
    @Autowired
    PassportInterceptor passportInterceptor;
    @Autowired
    LoginRequredInterceptor loginRequredInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(passportInterceptor);
        //当系统初始化，把自己定义好的passaportInterceptor拦截器注册，加入到整个链路上
        //这样以后所有的请求都会走我的拦截器
        registry.addInterceptor(loginRequredInterceptor).addPathPatterns("/user/*");//当要访问../user/*时候要走这个拦截器
        //这个拦截器要写到上一个后面，因为第二个拦截器里用到的hostholder是第一个拦截器设置的
        super.addInterceptors(registry);
    }
}

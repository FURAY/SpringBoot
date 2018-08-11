package com.example.demo.model;

import org.springframework.stereotype.Component;

@Component
//上下文，用于拦截器preHandle取出的User，用于访问每个页面时进行权限判断
public class HostHolder {
    //private static  User user; //如果这样写的话，当有两个线程时访问的是同一个User
    private static ThreadLocal<User> users=new ThreadLocal<User>();
    //看起来是一个变量，但是每个线程都有一个user拷贝，所占用的内存是不一样的，但是每个线程都可以通过这个公共接口访问得到
//相当于这里有个Map<ThreadID,User> 每个线程有它的ID，当去getUser时候都会返回对应的user
    public User getUser(){
        return users.get();
    }

    public void setUser(User user){
        users.set(user);
    }

    public void clear(){
        users.remove();
    }

}

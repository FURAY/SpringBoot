package com.example.demo.async;

import java.util.List;

public interface EventHandler {//从队列里出来后，有很多事件Event，这里是它们映射的地方分别有哪几个handler处理
    void doHandler(EventModel model);//用来Handler处理的事件
    List<EventType> getSupportEventTypes();//用来注册自己，让别人知道自己是关注哪一些eventType的
    //因为EventHandler可能有好几个event，所以用List
}

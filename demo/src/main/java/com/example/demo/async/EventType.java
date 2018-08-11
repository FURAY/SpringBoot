package com.example.demo.async;

/*
创建类的顺序
* EventType-EventModel-EventProducer-EventHandler-EventConsumer-LikeHandler-LikeController(在里面加个Eventproducer去fire对应event)
*
* */

public enum EventType {//表示异步事件的类型
    LIKE(0),
    COMMENT(1),
    LOGIN(2),
    MAIL(3),
    FOLLOW(4),
    UNFOLLOW(5);
    private int value;
    EventType(int value){
        this.value=value;
    }
    public int getValue(){
        return value;
    }

}

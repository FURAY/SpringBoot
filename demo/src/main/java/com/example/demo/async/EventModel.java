package com.example.demo.async;

import java.util.HashMap;
import java.util.Map;

public class EventModel {//事件的载体（事件公共部分），虽然事件都不同，但是不同事件都有公共的东西
    private EventType type;//事件类型，如type=1，评论事件
    private int actorId;//事件触发者，谁评论的
    private int entityType;//事件关联的触发载体，评论哪一个题目
    private int entityId;//事件关联的触发载体，评论哪一个题目
    private int entityOwnerId;//事件关联的触发载体对象，发信息给发问题的人说有人给你发信息了，一般站内信或人与人之间交互

    private Map<String,String> exts=new HashMap<String,String>();//扩展字段，因为发事件的时候有各种各样信息，要把它保留下来，有点类似ViewObject
    //上面那些字段也可以放进map，但是由于上面的太常用了，所以就单独分离出来方便使用


    public EventModel() {

    }

    public EventModel(EventType type) {
        this.type = type;
    }

    public EventType getType() {
        return type;
    }

    public EventModel setType(EventType type) {
        this.type = type;
        return this;
        //xx.setType.setActorId.setEntityType.setXXX()，可以由于set直接返回eventmodel，所有可以用一个链式的操作调用参数去设置，方便使用**
    }

    public int getActorId() {
        return actorId;
    }

    public EventModel setActorId(int actorId) {
        this.actorId = actorId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public EventModel setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public EventModel setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityOwnerId() {
        return entityOwnerId;
    }

    public EventModel setEntityOwnerId(int entityOwnerId) {
        this.entityOwnerId = entityOwnerId;
        return this;
    }

    public Map<String, String> getExts() {
        return exts;
    }

    public EventModel setExts(Map<String, String> exts) {
        this.exts = exts;
        return this;
    }
    public EventModel setExts(String key,String value){//写个简单的，直接传key和value，不用默认的set传map
        exts.put(key,value);
        return this;
    }
    public String getExts(String key){
        return exts.get(key);
    }
}

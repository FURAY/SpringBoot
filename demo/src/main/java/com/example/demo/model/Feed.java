package com.example.demo.model;

import com.alibaba.fastjson.JSONObject;

import java.util.Date;

public class Feed {
    private int id;
    private int type;
    private int userId;
    private Date createdDate;
    //JSON存储各种各样字段,不一样的Feed存进的date不一样，如转发个微博data可能就存个原始id？？，（不可能把转发的那个微博信息都存进来，因为数据量大且重复，不可能都拿来get），所以要用个JSONObject
    private String data;
    private JSONObject dateJSON=null;//这里放JSONObject和ViewObject一样的道理,ViewObject也有个get方法，模板直接从这里的get方法读取数据
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
        dateJSON=JSONObject.parseObject(data);
    }
    public String get(String key){
        return dateJSON==null?null:dateJSON.getString(key);
    }
    
}

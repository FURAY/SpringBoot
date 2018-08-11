package com.example.demo.async;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.util.JedisAdapter;
import com.example.demo.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventProducer {//是个事件的入口，由它统一去发这个事件
    @Autowired
    JedisAdapter jedisAdapter;//可以理解为队列，这里用redis作为队列，当然也可以用别的如多线程的BlockingQueue

    public boolean fireEvent(EventModel eventModel){//把eventModel发出去，即把事件保存到队列里
        try{
            String json=JSONObject.toJSONString(eventModel);//把事件转换成json然后放在redis队列里
            String key=RedisKeyUtil.getEventQueueKey();
            jedisAdapter.lpush(key,json);
            System.out.println("zxczxc");
            return true;
        }catch (Exception e){
            return false;
        }
    }

}

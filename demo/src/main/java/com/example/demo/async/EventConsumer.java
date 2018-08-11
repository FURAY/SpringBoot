package com.example.demo.async;



import com.alibaba.fastjson.JSON;
import com.example.demo.util.JedisAdapter;
import com.example.demo.util.RedisKeyUtil;
import com.example.demo.util.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class EventConsumer implements InitializingBean,ApplicationContextAware {//用于处理所有队列里面的事件
    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);
    private Map<EventType,List<EventHandler>> config=new HashMap<EventType,List<EventHandler>>();//看EventType是哪一种，然后去找它那一些EventHanlder去处理
    private ApplicationContext applicationContext;
    @Autowired
    JedisAdapter jedisAdapter;
    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String,EventHandler> beans=applicationContext.getBeansOfType(EventHandler.class);//从上下文去找，所有的实现EventHandler接口的类,把String是实现接口的类名，EventHandler是实现接口的类实体
        // （因为工程里面实现EventHandler接口的类有很多个，不可能一个个去配置（一个个去配置new一个对象），并且如果是后面新加实现EventHandler接口的类，无需更改，它自己会重新搜索并加入到里面）
        if (beans!=null){
            //System.out.println(beans.get("likeHandler"));
                for(Map.Entry<String,EventHandler> entry:beans.entrySet()){//遍历所有的实现EventHandler接口类的集合beans，
                List<EventType> eventTypes=entry.getValue().getSupportEventTypes();//把找到每一个关注的事件
                for(EventType type:eventTypes){
                    if (!config.containsKey(type)){
                        config.put(type,new ArrayList<EventHandler>());//注册事件
                    }
                    System.out.println(entry.getValue()+" !!! "+type+" !!! "+entry.getKey());
                    config.get(type).add(entry.getValue());//给注册添加好的事件添加Type对应的EventHandler
                }
            }
        }
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    String key=RedisKeyUtil.getEventQueueKey();
                    List<String> events=jedisAdapter.brpop(0,key);//从队列里面取被序列化的event
                    for(String msg:events){
                        if(msg.equals(key)) continue;//？？由于不知啥原因，第一个返回的是key字符串 所以要过滤掉
                        EventModel eventModel= JSON.parseObject(msg,EventModel.class);//反序列化
                        if(!config.containsKey(eventModel.getType())){
                            logger.error("不能识别的事件");
                            continue;
                        }
                        for (EventHandler handler:config.get(eventModel.getType())){
                            handler.doHandler(eventModel);
                        }
                    }

                }
            }
        });
        thread.start();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;//存储上下文
    }
}

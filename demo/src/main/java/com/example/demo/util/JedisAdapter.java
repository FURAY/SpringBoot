package com.example.demo.util;

import com.example.demo.service.SensitiveService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

import java.util.List;
import java.util.Set;

@Service
public class JedisAdapter implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(JedisAdapter.class);
    private JedisPool pool;


    public static void print(int index,Object obj){
        System.out.println(String.format("%d,%s",index,obj.toString()));

    }
    public static void main(String[] args){
        Jedis jedis = new Jedis("redis://localhost:6379/9");
        jedis.flushDB();

        // get set
        jedis.set("hello", "world");
        print(1, jedis.get("hello"));
        jedis.rename("hello", "newhello");
        print(1, jedis.get("newhello"));
        jedis.setex("hello2", 1800, "world");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        pool=new JedisPool("redis://localhost:6379/9");
    }

    public long  sadd(String keys,String value){
        Jedis jedis =null;
        try{
            jedis=pool.getResource();
            return jedis.sadd(keys,value);
        }catch (Exception e){
            logger.error("jedis发生异常1"+e.getMessage());
        }finally {
            if(jedis!=null){
                jedis.close();
            }
        }
        return 0;
    }
    public long  srem(String keys,String value){
        Jedis jedis =null;
        try{
            jedis=pool.getResource();
            return jedis.srem(keys,value);
        }catch (Exception e){
            logger.error("jedis发生异常2"+e.getMessage());
        }finally {
            if(jedis!=null){
                jedis.close();
            }
        }
        return 0;
    }
    public long  scard(String keys){
        Jedis jedis =null;
        try{
            jedis=pool.getResource();
            return jedis.scard(keys);
        }catch (Exception e){
            logger.error("jedis发生异常3"+e.getMessage());
        }finally {
            if(jedis!=null){
                jedis.close();
            }
        }
        return 0;
    }
    public boolean  sismember(String keys,String value){
        Jedis jedis =null;
        try{
            jedis=pool.getResource();
            return jedis.sismember(keys,value);
        }catch (Exception e){
            logger.error("jedis发生异常4"+e.getMessage());
        }finally {
            if(jedis!=null){
                jedis.close();
            }
        }
        return false;
    }
    public List<String> brpop(int timeout,String key){
        Jedis jedis=null;
        try {
            jedis=pool.getResource();
            return jedis.brpop(timeout,key);
        }catch (Exception e){
            logger.error("jedis发生异常5"+e.getMessage());
        }finally {
            if (jedis!=null)
                jedis.close();
        }
        return null;
    }
    public long lpush(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.lpush(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }
    public Jedis getJedis(){
        return pool.getResource();
    }//？？因为transaction是里面获取的
    public Transaction multi(Jedis jedis){
        try{
            return jedis.multi();//开启事务
        }catch (Exception e){
            logger.error("开启事务出错"+e.getMessage());
        }
        return null;
    }
    public List<Object> exec(Transaction tx,Jedis jedis){
        try {
            return tx.exec();
        }catch (Exception e){
            logger.error("exec"+e.getMessage());
        }finally {
            if (tx!=null){
                try {
                    tx.close();
                }catch (Exception e){
                    logger.error("tx关掉出错"+e.getMessage());
                }
            }
            if (jedis!=null){
                jedis.close();
            }
        }
        return null;
    }
    public long zadd(String key,double score, String value){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zadd(key,score,value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }
    public Set<String> zrevrange(String key, int start, int end){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zrevrange(key,start,end);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }
    public long zcard(String key){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zcard(key);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }
    public Double zscore(String key,String member){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zscore(key,member);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }
}

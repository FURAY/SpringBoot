package com.example.demo.dao;

import com.example.demo.model.LoginTicket;
import org.apache.ibatis.annotations.*;

@Mapper
public interface LoginTicketDAO {
    String TABLE_NAME = "login_ticket";
    String INSERT_FIELDS = " user_id, expired, status, ticket ";
    String SELECT_FIELDS = " id, user_id, expired, status, ticket ";
//这里的values中的值是loginTicket里的，如#{user_id}是通过loginTicket.getUser_id得出来的
    @Insert({"insert into "+TABLE_NAME+"("+INSERT_FIELDS+") values (#{userId},#{expired},#{status},#{ticket})"})
    int addTicket(LoginTicket loginTicket);

    //这里LoginTicket中User的id 是 user_id，数据库也是user_id,但是好像mybatis会自动把数据库中user_id转为userId，所有Bean里要写出userId
    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where ticket=#{ticket}"})
    LoginTicket selectByTicket(String ticket);

    @Update({"update "+TABLE_NAME+" set status=#{status} where ticket=#{ticket}"})
    void updateStatus(@Param("ticket") String ticket,@Param("status") int status);

    @Delete({"delete form "+TABLE_NAME+" where user_id=#{user_id}"})
    void deleteById(int user_id);
}
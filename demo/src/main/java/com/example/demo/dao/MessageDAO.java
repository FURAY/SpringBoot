package com.example.demo.dao;

import com.example.demo.model.Comment;
import com.example.demo.model.Message;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MessageDAO {
    String TABLE_NAME = " message ";
    String INSERT_FIELDS = " from_id, to_id, content, has_read, conversation_id, created_date ";
    String SELECT_FIELDS = " id, from_id, to_id, content, has_read, conversation_id, created_date ";

    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS,
            ") values (#{fromId},#{toId},#{content},#{hasRead},#{conversationId},#{createdDate})"})
    int addMessage(Message message);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where conversation_id=#{conversationId} order by created_date desc limit #{offest},#{limit}" })
    List<Message> selectConversationDetail(@Param("conversationId") String conversationId,//这里conversationId可以是String可以是int，但是这里是conversation是fromid和toid组合，所以可能会产生很大的数字，所以简单得用string然后get方法用fromid_toid
                                        @Param("offest") int offest,
                                           @Param("limit") int limit);//分页，从offest开始，取limit个

    @Select({"select ", INSERT_FIELDS, " ,count(id) as id from ( select * from ", TABLE_NAME, " where from_id=#{userId} or to_id=#{userId} order by created_date desc) tt group by conversation_id  order by created_date desc limit #{offset}, #{limit}"})
/*    @Select({"select ", INSERT_FIELDS, ",count(id) as id from(select * from ", TABLE_NAME, "where from_id=#{userId} or to_id=#{userId} order  by created_date desc) " +
            "tt group by conversation_id order by created_date desc limit #{offest},#{limit}" })*/
    //先看括号里的先从表中选了 根据id的数据 然后group by conversation_id 就是唯一一条 分组的意思 比如说有2条一样的conversation_id数据 选择最上的数据，所以在选择之前我们还要by created_date desc降序排序 选择时间最早的
    //然后因为这里的id没有在list网页有作用所以id用来存有多少条id，即在筛选上面那些之前再选出有多少条相同的converstion_id即多少条消息 存在id里，以及选出其他数据，并在选出这些数据时候再然后这些数据时间先后再排个序（即我和A，B,C,D的聊天时间最先谁的聊天在窗口最上面），然后再分页
    List<Message> selectConversationList(@Param("userId") int userId,
                                           @Param("offset") int offset,
                                           @Param("limit") int limit);//分页，从offest开始，取limit个

    @Select({"select count(id) from ", TABLE_NAME, " where has_read=0 and to_id=#{userId} and conversation_id=#{conversationId}"})
    int getConvesationUnreadCount(@Param("userId") int userId, @Param("conversationId") String conversationId);
    //选出别人传给自己的私信未读数,to_id代表自己的id，也就是别人发消息给自己的那条数据库
}

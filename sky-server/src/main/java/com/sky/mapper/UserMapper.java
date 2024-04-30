package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface UserMapper {

    @Select("select * from sky_take_out.user where openid = #{openid}")
    User queryByOpenid(String openid);


    //
    void insertUser(User user1);

    Integer countByMap(Map map);
}

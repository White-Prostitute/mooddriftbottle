package edu.scu.mooddriftbottlerebuild.dao;

import edu.scu.mooddriftbottlerebuild.entity.UsersEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 
 * 
 * @author liguohua
 * @email 3537136394@qq.com
 * @date 2023-05-20 00:09:36
 */
@Mapper
public interface UsersDao extends BaseMapper<UsersEntity> {

    @Update("update users set salvage_num = salvage_num + 1 where open_id = #{openid}")
    void setSalvageNum(String openid);

    @Select("select count(*) from session join bottle on bottle.bottle_id = session.bottle_id " +
            "where bottle.user_id = #{openid}")
    int getBeReplyNum(String openid);

    @Select("select count(*) from session where user_id = #{openid}")
    int getReplyNum(String openid);

    @Update("update users set violation = violation + 1 where open_id = #{openid}")
    void setViolation(String openid);

}

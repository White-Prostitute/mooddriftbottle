package edu.scu.mooddriftbottlerebuild.dao;

import edu.scu.mooddriftbottlerebuild.entity.BottleEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 
 * 
 * @author liguohua
 * @email 3537136394@qq.com
 * @date 2023-05-20 00:09:36
 */
@Mapper
public interface BottleDao extends BaseMapper<BottleEntity> {

    @Update("update bottle set checked = #{check} where bottle_id = #{bottle_id}")
    void checkBottle(@Param("bottle_id") int bottle_id, @Param("check") int check);

    @Select("select count(*) from bottle where user_id = #{user_id}")
    int getTotalNum(String user_id);

    @Select("select  count(*) from bottle where user_id  = #{user_id} and type = 1")
    int getGoodNum(String user_id);
}

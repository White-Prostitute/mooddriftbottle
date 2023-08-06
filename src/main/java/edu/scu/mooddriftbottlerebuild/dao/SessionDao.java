package edu.scu.mooddriftbottlerebuild.dao;

import edu.scu.mooddriftbottlerebuild.entity.SessionEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 
 * 
 * @author liguohua
 * @email 3537136394@qq.com
 * @date 2023-05-20 00:09:36
 */
@Mapper
public interface SessionDao extends BaseMapper<SessionEntity> {

    @Select("select * from session where session_id = (select min(session_id) from session where checked = 0 and logic = 0)")
    SessionEntity getUncheckedSession();

}

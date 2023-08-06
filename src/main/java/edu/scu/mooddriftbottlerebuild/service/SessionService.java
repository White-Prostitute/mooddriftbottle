package edu.scu.mooddriftbottlerebuild.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.scu.mooddriftbottlerebuild.entity.ReplyEntity;
import edu.scu.mooddriftbottlerebuild.entity.SessionEntity;
import edu.scu.mooddriftbottlerebuild.utils.PageUtils;

import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author liguohua
 * @email 3537136394@qq.com
 * @date 2023-05-20 00:09:36
 */
public interface SessionService extends IService<SessionEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<SessionEntity> getSessionList(String openid);

    SessionEntity getUncheckSession();

    void checkSession(SessionEntity entity, int check);

    List<SessionEntity> getSessionListByBottleId(int bottle_id);

    void updateSession(SessionEntity session);

    void deleteSession(int sessionId);
}


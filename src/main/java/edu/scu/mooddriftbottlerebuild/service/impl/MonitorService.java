package edu.scu.mooddriftbottlerebuild.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import edu.scu.mooddriftbottlerebuild.controller.response.StatisticResponse;
import edu.scu.mooddriftbottlerebuild.dao.BottleDao;
import edu.scu.mooddriftbottlerebuild.dao.ReplyDao;
import edu.scu.mooddriftbottlerebuild.dao.SessionDao;
import edu.scu.mooddriftbottlerebuild.dao.UsersDao;
import edu.scu.mooddriftbottlerebuild.entity.BottleEntity;
import edu.scu.mooddriftbottlerebuild.entity.SessionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MonitorService {

    @Autowired
    BottleDao bottleDao;

    @Autowired
    UsersDao usersDao;

    @Autowired
    SessionDao sessionDao;

    @Autowired
    ReplyDao replyDao;

    public StatisticResponse getStatisticData(){
        StatisticResponse response = new StatisticResponse();
        Integer totalUserNum = usersDao.selectCount(null);
        response.setTotalUserNum(totalUserNum);
        Integer totalBottleNum = bottleDao.selectCount(null);
        response.setTotalBottleNum(totalBottleNum);
        QueryWrapper<BottleEntity> bottleWp = new QueryWrapper<>();
        bottleWp.eq("checked", 0);
        Integer uncheckBottleNum = bottleDao.selectCount(bottleWp);
        response.setUncheckBottleNum(uncheckBottleNum);
        Integer totalSessionNum = sessionDao.selectCount(null);
        response.setTotalSessionNum(totalSessionNum);
        QueryWrapper<SessionEntity> sessionWp = new QueryWrapper<>();
        sessionWp.eq("checked", 0);
        Integer uncheckSessionNum = sessionDao.selectCount(sessionWp);
        response.setUncheckSessionNum(uncheckSessionNum);
        Integer totalReplyNum = replyDao.selectCount(null);
        response.setTotalReplyNum(totalReplyNum);
        return response;
    }

}

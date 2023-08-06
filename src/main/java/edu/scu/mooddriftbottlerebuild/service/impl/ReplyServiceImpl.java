package edu.scu.mooddriftbottlerebuild.service.impl;

import edu.scu.mooddriftbottlerebuild.dao.SessionDao;
import edu.scu.mooddriftbottlerebuild.entity.SessionEntity;
import edu.scu.mooddriftbottlerebuild.utils.PageUtils;
import edu.scu.mooddriftbottlerebuild.utils.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


import edu.scu.mooddriftbottlerebuild.dao.ReplyDao;
import edu.scu.mooddriftbottlerebuild.entity.ReplyEntity;
import edu.scu.mooddriftbottlerebuild.service.ReplyService;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.websocket.Session;


@Service("replyService")
public class ReplyServiceImpl extends ServiceImpl<ReplyDao, ReplyEntity> implements ReplyService {

    @Resource
    RedisTemplate<String, String> template;

    @Autowired
    SessionDao sessionDao;

    private static int maxSessionIdNow;

    @PostConstruct
    void init(){
        maxSessionIdNow = sessionDao.selectCount(null);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<ReplyEntity> page = this.page(
                new Query<ReplyEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    /**
     * 回复
     * 首先检查token
     * 如果是回复漂流瓶，则创建一个新的会话
     * @param reply 回复封装类
     * @param token 操作token
     */
    @Override
    public void reply(ReplyEntity reply, String token, int bottle_id) throws Exception {
        //检查token
        ValueOperations<String, String> ops = template.opsForValue();
        String key = reply.getUserId()+":token";
        String expectedToken = ops.get(key);
        //TODO 自定义异常类型
        if(expectedToken == null || !expectedToken.equals(token))throw new Exception();

        //token有效继续操作
        Date date = new Date();
        if(reply.getSessionId() == null){//如果是回复漂流瓶，则创建一个新的回复
            SessionEntity entity = new SessionEntity();
            entity.setBottleId(bottle_id);
            entity.setChecked(0);
            entity.setFirstSentence(reply.getContent());
            SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
            entity.setSessionCreateTime(formatter.format(date));
            entity.setUserId(reply.getUserId());
            maxSessionIdNow++;
            sessionDao.insert(entity);
        }
        reply.setSessionId(maxSessionIdNow);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        reply.setCreateTime(formatter.format(date));
        baseMapper.insert(reply);
    }

    /**
     * 根据会话id返回一段会话
     * @param sessionId 会话id
     * @return 会话内容
     */
    @Override
    public List<ReplyEntity> getReplyListBySessionId(int sessionId) {
        QueryWrapper<ReplyEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("session_id", sessionId);
        return baseMapper.selectList(wrapper);
    }

}
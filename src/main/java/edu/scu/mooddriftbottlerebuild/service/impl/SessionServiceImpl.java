package edu.scu.mooddriftbottlerebuild.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.scu.mooddriftbottlerebuild.config.ConstantConfig;
import edu.scu.mooddriftbottlerebuild.dao.BottleDao;
import edu.scu.mooddriftbottlerebuild.dao.ReplyDao;
import edu.scu.mooddriftbottlerebuild.dao.SessionDao;
import edu.scu.mooddriftbottlerebuild.dao.UsersDao;
import edu.scu.mooddriftbottlerebuild.entity.ReplyEntity;
import edu.scu.mooddriftbottlerebuild.entity.SessionEntity;
import edu.scu.mooddriftbottlerebuild.entity.UsersEntity;
import edu.scu.mooddriftbottlerebuild.service.SessionService;
import edu.scu.mooddriftbottlerebuild.utils.PageUtils;
import edu.scu.mooddriftbottlerebuild.utils.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Service("sessionService")
public class SessionServiceImpl extends ServiceImpl<SessionDao, SessionEntity> implements SessionService {

    @Resource
    RedisTemplate<String, String> template;

    @Autowired
    BottleDao bottleDao;

    @Autowired
    ReplyDao replyDao;

    @Autowired
    UsersDao usersDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SessionEntity> page = this.page(
                new Query<SessionEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    /**
     * 获取我的会话列表
     * @param openid 用户id
     * @return 会话列表
     */
    @Override
    public List<SessionEntity> getSessionList(String openid) {
        QueryWrapper<SessionEntity> wrapper = new QueryWrapper<>();
        //TODO 检查一段会话是否有新的回复
        wrapper.eq("user_id", openid);
        return baseMapper.selectList(wrapper);
    }

    /**
     * 获取一个还未审核的会话
     * @return 会话实体类
     */
    @Override
    public SessionEntity getUncheckSession() {
        return baseMapper.getUncheckedSession();
    }

    /**
     * 完成一个会话的审核
     * 审核通过则提醒用户有新的回复
     * @param entity 会话实体类
     * @param check 审核结果
     */
    @Override
    public void checkSession(SessionEntity entity, int check) {
        if(check == 1){//审核通过，添加缓存提醒用户有新的评论
            //首先需要查询是谁被回复了，即根据会话bottle_id找到bottle的user_id
            String beReplyUserId = bottleDao.selectById(entity.getBottleId()).getUserId();
            String key = beReplyUserId+":"+entity.getBottleId()+":reply";
            ValueOperations<String, String> ops = template.opsForValue();
            ops.set(key, "1");

            //添加积分
            UsersEntity usersEntity = usersDao.selectById(entity.getUserId());
            Integer score = usersEntity.getScore();
            usersEntity.setScore(score + ConstantConfig.Score.REPLY_SCORE);
            usersDao.updateById(usersEntity);
        }
        entity.setChecked(check);
        baseMapper.updateById(entity);
    }

    /**
     * 获取一个瓶子的所有会话(已经完成审核)
     * 删除提醒该瓶子有最新回复的缓存 (缓存key格式 user_id:bottle_id:reply)
     * @param bottle_id 瓶子id
     * @return 会话实体类列表
     */
    @Override
    public List<SessionEntity> getSessionListByBottleId(int bottle_id) {
        QueryWrapper<SessionEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("bottle_id", bottle_id);
        wrapper.eq("checked", 1);
        //删除缓存
        String pattern = "*"+bottle_id+":reply";
        Set<String> keys = template.keys(pattern);
        if(keys != null && !keys.isEmpty()){
            template.delete(keys);
        }
        return baseMapper.selectList(wrapper);
    }

    /**
     * 更新一个还未完成审核的会话
     * @param session 对话实体类
     */
    @Override
    public void updateSession(SessionEntity session) {
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        session.setSessionCreateTime(formatter.format(new Date()));
        baseMapper.updateById(session);
        QueryWrapper<ReplyEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("session_id", session.getSessionId());
        ReplyEntity replyEntity = replyDao.selectOne(wrapper);
        replyEntity.setCreateTime(session.getSessionCreateTime());
        replyEntity.setContent(session.getFirstSentence());
        replyDao.updateById(replyEntity);
    }

    /**
     * 删除一段会话
     */
    @Override
    public void deleteSession(int sessionId) {
        baseMapper.deleteById(sessionId);
    }

}
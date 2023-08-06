package edu.scu.mooddriftbottlerebuild.service.impl;

import edu.scu.mooddriftbottlerebuild.dao.BottleDao;
import edu.scu.mooddriftbottlerebuild.dao.ReplyDao;
import edu.scu.mooddriftbottlerebuild.dao.SessionDao;
import edu.scu.mooddriftbottlerebuild.entity.ReplyEntity;
import edu.scu.mooddriftbottlerebuild.utils.PageUtils;
import edu.scu.mooddriftbottlerebuild.utils.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.*;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.scu.mooddriftbottlerebuild.dao.UsersDao;
import edu.scu.mooddriftbottlerebuild.entity.UsersEntity;
import edu.scu.mooddriftbottlerebuild.service.UsersService;

import javax.annotation.Resource;


@Service("usersService")
public class UsersServiceImpl extends ServiceImpl<UsersDao, UsersEntity> implements UsersService {

    @Autowired
    BottleDao bottleDao;

    @Autowired
    SessionDao sessionDao;

    @Autowired
    ReplyDao replyDao;

    @Resource
    RedisTemplate<String, String> template;

    //举报列表
    HashSet<Integer> reportSet = new HashSet<>();

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<UsersEntity> page = this.page(
                new Query<UsersEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    /**
     * 获取用户记录，用于展示个人报告
     * @param openid 用户id
     * @return 个人数据
     */
    @Override
    public Map<String, Integer> getUserRecord(String openid) {
        Map<String, Integer> map = new HashMap<>();
        //扔出的瓶子总数
        int totalNum = bottleDao.getTotalNum(openid);
        //好心情瓶总数
        int goodNum = bottleDao.getGoodNum(openid);
        //坏心情瓶总数
        int badNum = totalNum - goodNum;
        UsersEntity user = baseMapper.selectById(openid);
        //打捞数
        int salvageNum = user.getSalvageNum();
        //回复数
        int replyNum = baseMapper.getReplyNum(openid);
        //被回复数
        int beReplyNum = baseMapper.getBeReplyNum(openid);

        map.put("total_num", totalNum);
        map.put("good_num", goodNum);
        map.put("bad_num", badNum);
        map.put("salvage_num", salvageNum);
        map.put("reply_num", replyNum);
        map.put("be_reply_num", beReplyNum);

        return map;
    }

    /**
     * 举报用户生效
     * @param openid 被举报用户id列表
     * @param sessionId 会话Id
     */
    @Override
    public void violation(String[] openid, Integer sessionId) {
        //删除举报表中该对话
        reportSet.remove(sessionId);
        System.out.println("举报上传");
        //如果存在违规情况删除对话
        if(openid.length != 0)sessionDao.deleteById(sessionId);
        for (String s : openid) {
            //添加缓存,告知用户被举报
            ValueOperations<String, String> ops = template.opsForValue();
            String key = s + ":violation";
            ops.set(key, "1");
            UsersEntity user = baseMapper.selectById(s);
            if(user != null){
                if (user.getViolation() >= 2){//这是第三次违规了
                    ops.set(s + ":forbidden", "1");
                }
            }
            baseMapper.setViolation(s);
        }
    }

    /**
     * 用户举报
     * @param sessionId 举报的会话id
     */
    @Override
    public void receiveReport(int sessionId) {
        reportSet.add(sessionId);
    }

    /**
     * 获取一段被举报的对话
     * @return 对话内容
     */
    @Override
    public List<ReplyEntity> getReportedReplyList() {
        Iterator<Integer> iterator = reportSet.stream().iterator();
        int sessionId = -1;
        if(iterator.hasNext())sessionId = iterator.next();
        QueryWrapper<ReplyEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("session_id", sessionId);
        return replyDao.selectList(wrapper);
    }

    /**
     * 判断用户是否已经注册
     * @param open_id 用户id
     * @return 用户注册与否
     */
    @Override
    public boolean isRegister(String open_id) {
        UsersEntity entity = baseMapper.selectById(open_id);
        return entity != null;
    }


}
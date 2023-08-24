package edu.scu.mooddriftbottlerebuild.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.scu.mooddriftbottlerebuild.config.ConstantConfig;
import edu.scu.mooddriftbottlerebuild.controller.reqeust.CheckBottleRequest;
import edu.scu.mooddriftbottlerebuild.dao.BottleDao;
import edu.scu.mooddriftbottlerebuild.dao.UsersDao;
import edu.scu.mooddriftbottlerebuild.entity.BottleEntity;
import edu.scu.mooddriftbottlerebuild.entity.UsersEntity;
import edu.scu.mooddriftbottlerebuild.service.BottleService;
import edu.scu.mooddriftbottlerebuild.utils.PageUtils;
import edu.scu.mooddriftbottlerebuild.utils.Query;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;


@Slf4j
@Service("bottleService")
public class BottleServiceImpl extends ServiceImpl<BottleDao, BottleEntity> implements BottleService {

    @Autowired
    UsersDao usersDao;

    @Resource
    RedisTemplate<String, String> template;

    //存放还未审核的瓶子的id
    private static  final List<Integer> uncheckedIds = new ArrayList<>();

    private static int maxIdNow;

    //用于充当锁，防止并发修改积分
    private static final Object scoreLock = new Object();

    /**
     * 初始化BottleService,将未检查的瓶子ID放入集合当中
     */
    @PostConstruct
    public void init(){
        QueryWrapper<BottleEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("checked", 0);
        wrapper.select("bottle_id");
        List<Object> objects = baseMapper.selectObjs(wrapper);
        objects.forEach(o->uncheckedIds.add((Integer) o));
        maxIdNow = baseMapper.selectCount(null);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<BottleEntity> page = this.page(
                new Query<BottleEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    /**
     * 获取一个不是自己的已经完成审核的瓶子
     * @param openid 打捞者的用户Id
     * @param mode 打捞模式 -> 只打捞好的；只打捞坏的；随机
     * @return 一个瓶子实体类
     */
    @Override
    public BottleEntity getOnBottleChecked(String openid, Integer mode) {
        List<Object> ids;
        QueryWrapper<BottleEntity> wrapper = new QueryWrapper<>();
        wrapper.select("bottle_id");
        wrapper.eq("checked", 1);
        wrapper.ne("user_id", openid);
        if(mode != 2){
            wrapper.eq("type", mode);
        }
        ids = baseMapper.selectObjs(wrapper);

        int total = ids.size();
        if(total <= 0) return null;
        Random rd = new Random();
        int i = rd.nextInt(total);
        BottleEntity bottle = baseMapper.selectById((Integer)ids.get(i));
        //打捞数加1
        usersDao.setSalvageNum(openid);
        return bottle;
    }

    /**
     * 获取一个未检查的瓶子(创建日期最早的)
     * 因为上面已经在List中存放了未检查的瓶子的id，所以只需要返回id最小的那个即可,即第一个
     */
    @Override
    public BottleEntity getOneBottleUnChecked() {
        if(uncheckedIds.size() == 0){
            return null;
        }
        Integer id = uncheckedIds.get(0);
        return baseMapper.selectById(id);
    }

    /**
     * 完成瓶子的审核:
     * 1. 将瓶子id从未检查列表中移除
     * 2. 如果是驳回则添加缓存提醒用户
     * 3. 如果审核员有回复则新增回复
     */
    @Override
    public void checkBottle(CheckBottleRequest request) {
        //TODO 审核员的回复

        log.info("编号为{}的瓶子被{}了", request.getBottleId(), request.getCheck()==1?"通过":"驳回");

        //将瓶子id从未检查列表移除
        synchronized (uncheckedIds){
            if(request.getBottleId() == uncheckedIds.get(0)){
                uncheckedIds.remove(0);
            }
        }
        //如果驳回添加缓存
        if(request.getCheck() == 2){
            String userId = baseMapper.selectById(request.getBottleId()).getUserId();
            //TODO 修改了缓存存储方式
            SetOperations<String, String> set = template.opsForSet();
            set.add("overruleSet", userId);
        }else if(request.getCheck() == 1){
            //瓶子通过，获得积分
            synchronized (scoreLock){
                String userId = request.getUserId();
                UsersEntity usersEntity = usersDao.selectById(userId);
                if(Objects.nonNull(usersEntity)){
                    Integer score = usersEntity.getScore();
                    usersEntity.setScore(score + ConstantConfig.Score.BOTTLE_SCORE);
                    usersDao.updateById(usersEntity);
                }
            }
        }
        baseMapper.checkBottle(request.getBottleId(), request.getCheck());
    }

    /**
     * 添加一个瓶子
     * 需要先检查token,请求携带正确的token才可以进行操作
     * @param bottle 瓶子封装对象(缺少属性->时间)
     */
    @Override
    public void addBottle(BottleEntity bottle, String token) throws Exception {
        //首先检查token
        ValueOperations<String, String> ops = template.opsForValue();
        String key = bottle.getUserId()+":token";
        String expectedToken = ops.get(key);
        //TODO 编写自己的异常类型
        if(expectedToken == null || !expectedToken.equals(token))throw new Exception();

        //token有效，删除token并继续操作
        template.delete(key);
//        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        Date date = new Date(System.currentTimeMillis());
        bottle.setCreateTime(date);
        baseMapper.insert(bottle);

        //更新未检查列表以及最大编号
        maxIdNow++;
        uncheckedIds.add(maxIdNow);
    }

    /**
     * 获取我扔出的所有瓶子
     * 1. 预处理，只返回内容的前面一部分，用于展示预览
     * 2. 检查缓存查看是否有新的评论
     * @param openid 用户id
     * @return 瓶子列表
     */
    @Override
    public List<BottleEntity> getMyBottleList(String openid) {
        QueryWrapper<BottleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", openid);
        List<BottleEntity> list = baseMapper.selectList(queryWrapper);
        //预处理
        for (BottleEntity bottle : list) {
            //只取内容的前面一部分作为预览
            String s = bottle.getContent();
            if(s.length() > 15){
                s = s.substring(0, 15);
            }
            bottle.setContent(s);

            //检查缓存查看是否有新的评论
            String key =openid +  ":" + bottle.getBottleId() + ":reply";
            Boolean hasKey = template.hasKey(key);
            bottle.setHasNewReply(hasKey != null && hasKey);
        }
        return list;
    }

    /**
     * 逻辑删除一个瓶子
     * @param bottle_id 瓶子id
     * @param openid 用户id
     */
    @Override
    public void removeBottle(int bottle_id, String openid) {
        //TODO 处理缓存问题,添加缓存
        QueryWrapper<BottleEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("bottle_id", bottle_id);
        BottleEntity entity = baseMapper.selectById(bottle_id);
        if(entity.getChecked() == 0){
            synchronized (uncheckedIds){
                uncheckedIds.remove((Integer) bottle_id);
            }
        }
        baseMapper.delete(wrapper);

    }


}
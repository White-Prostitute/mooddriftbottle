package edu.scu.mooddriftbottlerebuild.controller;

import com.alibaba.fastjson.JSONObject;
import edu.scu.mooddriftbottlerebuild.entity.UsersEntity;
import edu.scu.mooddriftbottlerebuild.entity.WeiChatResponse;
import edu.scu.mooddriftbottlerebuild.service.UsersService;
import edu.scu.mooddriftbottlerebuild.utils.AliOss;
import edu.scu.mooddriftbottlerebuild.utils.R;
import edu.scu.mooddriftbottlerebuild.utils.RequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;


/**
 * 
 *
 * @author liguohua
 * @email 3537136394@qq.com
 * @date 2023-05-20 00:09:36
 */
@RestController
@RequestMapping("/users")
public class UsersController {
    @Autowired
    private UsersService usersService;

    @Resource
    RedisTemplate<String, String> template;

    /**
     * 获取个人使用记录
     * 打捞数，回复数，被回复数，好瓶子数，坏瓶子数，总数
     * @param openid 用户id
     * @return 记录数据
     */
    @GetMapping("/record")
    R getUserRecord(String openid){
        Map<String, Integer> record = usersService.getUserRecord(openid);
        return R.ok().put("data", record);
    }

    /**
     * 获取用户信息
     * @param openid 用户id
     * @return 用户信息
     */
    @GetMapping("/info")
    R getUserInfo(String openid){
        UsersEntity user = usersService.getById(openid);
        return R.ok().put("data", user);
    }

    /**
     *从OSS中获取用户头像并以流的形式返回
     * @param response 响应体
     * @param openid 用户id
     * @throws IOException IO异常
     */
    @GetMapping("/get_avatar")
    void getAvatarFromOss(HttpServletResponse response, String openid) throws IOException {
        InputStream fis = AliOss.getFile(openid + ".png");
        ServletOutputStream outputStream = response.getOutputStream();
        byte[] bytes = new byte[1024];
        int len;
        while((len = fis.read(bytes))!=-1){
            outputStream.write(bytes, 0, len);
        }
        fis.close();
        outputStream.close();
    }

    /**
     * 修改用户的头像
     * @param openid 用户唯一id
     * @param avatar 用户头像二进制流
     * @return 成功
     */
    @RequestMapping("/change_avatar")
    R changeAvatar(String openid, MultipartFile avatar) throws IOException {
        //存放用户的头像
        long size = avatar.getSize();
        if(size > 1024*1024){//大小限制在2M
            return R.error("imgError");
        }
        //采用阿里云Oss服务存放头像
        AliOss.upload(avatar.getInputStream(), openid+".png");
        return R.ok();
    }

    /**
     * 修改用户昵称
     * @param user 用户实体类
     * @return 操作成功与否
     */
    @PutMapping("/modify_nickname")
    R changeNickName(@RequestBody UsersEntity user){
        try{
            System.out.println(user);
            usersService.updateById(user);
        }catch (Exception e){
            R.error().put("exception", e.toString());
        }
        return R.ok();
    }

    /**
     * 检查用户是否有新的回复，违规
     * @param user_id 用户id
     * @return 是否有相关通知
     */
    @GetMapping("/notice")
    R checkNotice(String user_id){
        Map<String, Integer> map = new HashMap<>();
        Set<String> oKeys = template.keys(user_id + ":*:overrule");
        //检查是否有评论
        if(template.hasKey(user_id + ":*:reply") == Boolean.TRUE)map.put("reply",1);
        //检查是否有评论的回复
        if(template.hasKey(user_id + ":*:replyOfReply") == Boolean.TRUE)map.put("replyOfReply", 1);
        //检查是否有违规
        if(template.hasKey(user_id + ":violation") == Boolean.TRUE){
            map.put("violation", 1);
            template.delete(user_id+":violation");
        }
        assert oKeys != null;
        map.put("overrule", oKeys.size());
        //告诉用户有瓶子被驳回后，下次登录就不在提醒
        template.delete(oKeys);
        return R.ok().put("data", map);
    }

    /**
     * 用户注册
     * @param openid 用户微信openid
     * @param nickname 用户昵称
     * @param avatar 用户头像文件
     * @return 操作成功与否
     */
    @PostMapping("/register")
    String register(String openid, String nickname, MultipartFile avatar) throws IOException {
        //首先判断用户是否按照正常流程获取到了openid
        String pattern = openid + ":registry";
        Set<String> keys = template.keys(pattern);
        if(keys == null || keys.isEmpty())return "error";
        template.delete(keys);
        //将用户的id和昵称添加到数据库
        UsersEntity users = new UsersEntity();
        users.setNickName(nickname);
        users.setOpenId(openid);
        usersService.save(users);
        //存放用户的头像，首先检查用户上传的图片大小，如果太大，则不给予保存
        long size = avatar.getSize();
        if(size > 1024*1024){//大小限制在2M
            return "imgError";
        }
        //Oss对象存储
        AliOss.upload(avatar.getInputStream(), openid+".png");
        return "success";
    }

    /**
     * 获取操作用的token
     * @param openid 用户id
     * @return token
     */
    @GetMapping("/token")
    R getToken(String openid){
        UUID uuid = UUID.randomUUID();
        ValueOperations<String, String> ops = template.opsForValue();
        ops.set(openid+":token", uuid.toString(), Duration.ofMinutes(10));
        return R.ok().put("token", uuid.toString());
    }

    /**
     *用户登录，获取openid
     * @param code 从微信官方获取的用户获取用户信息的code
     * @return 用户openid
     * @throws IOException IO
     */
    @GetMapping("/get_weiChat_id")
    WeiChatResponse getWeiChatOpenId(String code) throws IOException {
        String s = "https://api.weixin.qq.com/sns/jscode2session?appid=wx94658494ef465471&secret=c008e12a4f7936dd629013ff114b655b&js_code=" + code;
        String get = RequestUtil.doGet(s, "UTF-8");
        WeiChatResponse response = JSONObject.parseObject(get, WeiChatResponse.class);
        String open_id = response.getOpenid();
        //openid为空，可能是非正常操作
        if(open_id == null)return null;
        //判断用户是否已经注册
        boolean b = usersService.isRegister(open_id);
        ValueOperations<String, String> ops = template.opsForValue();
        if(b){
            //如果用户已经登录，则授予半个小时的使用权,以及10条消息的限制
            String key = open_id + ":use";
            ops.set(key, "30", Duration.ofMinutes(30));
        }else{
            //如果用户未注册,则进入添加缓存等待用户注册
            String key = open_id + ":registry";
            ops.set(key, "1", Duration.ofMinutes(5));
        }
        response.setRegistered(b);
        //检查用户是否被加入黑名单
        Boolean isForbidden = template.hasKey(open_id + ":forbidden");
        response.setForbidden(isForbidden!=null?isForbidden:false);
        return response;
    }
}

package edu.scu.mooddriftbottlerebuild.controller;

import edu.scu.mooddriftbottlerebuild.entity.SessionEntity;
import edu.scu.mooddriftbottlerebuild.service.SessionService;
import edu.scu.mooddriftbottlerebuild.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 
 *
 * @author liguohua
 * @email 3537136394@qq.com
 * @date 2023-05-20 00:09:36
 */
@RestController
@RequestMapping("/session")
public class SessionController {

    @Autowired
    private SessionService sessionService;


    /**
     * 获取我的所有会话
     * @param openid 用户id
     * @return 会话列表
     */
    @GetMapping("/list")
    R getSessionList(String openid){
        List<SessionEntity> list = sessionService.getSessionList(openid);
        return R.ok().put("data", list);
    }

    /**
     * 获取一个未审核的会话
     * @return 会话实体类
     */
    @GetMapping("/uncheck_session")
    R getUncheckedSession(){
        SessionEntity session = sessionService.getUncheckSession();
        return R.ok().put("data", session);
    }

    /**
     * 审核一个会话，即漂流瓶的回复
     * @param entity 会话实体类
     * @param check 审核结果
     * @return 操作成功与否 success/exception
     */
    @PutMapping("/check_session")
    R checkSession(SessionEntity entity, int check){
        try{
            sessionService.checkSession(entity, check);
        }catch (Exception e){
            return R.error().put("exception", e.toString());
        }
        return R.ok();
    }

    /**
     * 获取一个漂流瓶的所有对话
     * @param bottle_id 瓶子id
     * @return 对话列表
     */
    @GetMapping("/bottle_session")
    R getSessionListByBottleId(int bottle_id){
        List<SessionEntity> list = sessionService.getSessionListByBottleId(bottle_id);
        return R.ok().put("data", list);
    }

    /**
     * 用户在会话完成审核前可以修改会话的内容
     * @param session 对话实体类
     * @return 操作成功与否 success / exception
     */
    @PutMapping("/update_session")
    R updateSession(@RequestBody SessionEntity session){
        try{
            sessionService.updateSession(session);
        }catch (Exception e){
            return R.error().put("exception", e.toString());
        }
        return R.ok();
    }


    /**
     * 删除一段对话
     * @param sessionId 对话id
     * @return 操作成功与否 success / exception
     */
    @DeleteMapping("/delete")
    R deleteSession(int sessionId){
        try{
            sessionService.deleteSession(sessionId);
        }catch (Exception e){
            return R.error().put("exception", e.toString());
        }
        return R.ok();
    }

}

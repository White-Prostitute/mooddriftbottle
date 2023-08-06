package edu.scu.mooddriftbottlerebuild.controller;

import edu.scu.mooddriftbottlerebuild.entity.ReplyEntity;
import edu.scu.mooddriftbottlerebuild.service.UsersService;
import edu.scu.mooddriftbottlerebuild.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/check")
public class CheckController {

    @Autowired
    UsersService usersService;


    /**
     * 审核举报结果
     * @param map 参数
     * @return 操作成功与否
     */
    @RequestMapping("/violation")
    R violation(@RequestBody HashMap<String, Object> map){
        try{
            ArrayList<String> list = (ArrayList<String>)map .get("openid");
            Integer sessionId = (Integer) map.get("sessionId");
            String[] openid = list.toArray(new String[0]);
            usersService.violation(openid, sessionId);
        }catch (Exception e){
            e.printStackTrace();
            return R.error().put("data", e.toString());
        }
        return R.ok();
    }


    /**
     * 上传举报
     * @param sessionId 会话id
     * @return 操作是否成功
     */
    @PostMapping("/report")
    R report(int sessionId){
        try{
            usersService.receiveReport(sessionId);
        }catch (Exception e){
            return R.error().put("exception", e.toString());
        }
        return R.ok();
    }

    /**
     * 获取一个被举报的会话
     * @return 会话内容
     */
    @GetMapping("/reported_session")
    R getReportedReplyList(){
        List<ReplyEntity> list = usersService.getReportedReplyList();
        return R.ok().put("data", list);
    }
}

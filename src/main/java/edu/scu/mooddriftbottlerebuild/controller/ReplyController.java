package edu.scu.mooddriftbottlerebuild.controller;

import edu.scu.mooddriftbottlerebuild.entity.ReplyEntity;
import edu.scu.mooddriftbottlerebuild.service.ReplyService;
import edu.scu.mooddriftbottlerebuild.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;


/**
 * 
 *
 * @author liguohua
 * @email 3537136394@qq.com
 * @date 2023-05-20 00:09:36
 */
@RestController
@RequestMapping("/reply")
public class ReplyController {

    @Autowired
    private ReplyService replyService;

    /**
     * 回复(回复漂流瓶或者进行对话)
     * @param reply 回复封装对象
     * @param token 操作token
     * @return 操作成功与否 success / exception
     */
    @PostMapping("/reply")
    R reply(@RequestBody ReplyEntity reply, String token, int bottle_id){
        if(reply.getContent() == null || reply.getContent().length() == 0)return R.error("no content");
        try{
            replyService.reply(reply, token, bottle_id);
        }catch (Exception e){
            e.printStackTrace();
            return R.error().put("exception", e.toString());
        }
        return R.ok();
    }

    /**
     * 获取一条会话
     * @param sessionId 对话id
     * @return 对话内容
     */
    @GetMapping("/get_reply_list")
    R getReplyListBySessionId(int sessionId){
        List<ReplyEntity> list = replyService.getReplyListBySessionId(sessionId);
        return R.ok().put("data", list);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{replyId}")
    public R info(@PathVariable("replyId") Integer replyId){
		ReplyEntity reply = replyService.getById(replyId);

        return R.ok().put("reply", reply);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody ReplyEntity reply){
		replyService.save(reply);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody ReplyEntity reply){
		replyService.updateById(reply);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Integer[] replyIds){
		replyService.removeByIds(Arrays.asList(replyIds));

        return R.ok();
    }

}

package edu.scu.mooddriftbottlerebuild.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.scu.mooddriftbottlerebuild.entity.ReplyEntity;
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
public interface ReplyService extends IService<ReplyEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void reply(ReplyEntity reply, String token, int bottle_id) throws Exception;

    List<ReplyEntity> getReplyListBySessionId(int sessionId);
}


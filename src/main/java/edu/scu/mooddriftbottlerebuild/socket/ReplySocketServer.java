package edu.scu.mooddriftbottlerebuild.socket;

import com.alibaba.fastjson.JSONObject;
import edu.scu.mooddriftbottlerebuild.entity.ReplyEntity;
import edu.scu.mooddriftbottlerebuild.service.ReplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

@CrossOrigin
@Component
@ServerEndpoint(value = "/socket/{openid}/{belong_id}")
public class ReplySocketServer {

    public static ReplyService service;

    private static final ConcurrentHashMap<String, Session> map = new ConcurrentHashMap<>();
    private int sessionId;
    private String openid;

    @Autowired
    public void setService(ReplyService service) {
        ReplySocketServer.service = service;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("openid") String openid, @PathParam("belong_id") int sessionId) {
        this.openid = openid;
        this.sessionId = sessionId;
        map.put(openid, session);
    }

    @OnClose
    public void onClose(Session session, @PathParam("openid") String openid) {
        map.remove(openid);
    }

    @OnMessage
    public void onMessage(String message, Session session) throws Exception {
        //解析信息
        StringBuilder to = new StringBuilder();
        String msg = "";
        for (int i = 0; i < message.length(); i++) {//获取接收方openid
            if (message.charAt(i) == ':') {
                msg = message.substring(i + 1);
                break;
            } else to.append(message.charAt(i));
        }
        //TODO 完善socket
        //将消息持久化存储
        ReplyEntity reply = new ReplyEntity();
        reply.setContent(msg);
        reply.setSessionId(sessionId);
        reply.setUserId(openid);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        Date date = new Date(System.currentTimeMillis());
        reply.setCreateTime(formatter.format(date));

        //检查对方是否在线
        Session toSession = map.get(to.toString());
        String jsonStr = JSONObject.toJSONString(reply);
        Session mySession = map.get(openid);
        //给自己也发一个
        mySession.getBasicRemote().sendText(jsonStr);
        if (toSession != null) {//在线就向对方推送消息
            toSession.getBasicRemote().sendText(jsonStr);
//            service.reply(reply, null);//不加缓存
        }else{//不在线
//            service.reply(reply, to.toString());
        }
        service.save(reply);
        //添加缓存，用于提醒(被回复的用户)
    }

}

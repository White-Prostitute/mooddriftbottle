package edu.scu.mooddriftbottlerebuild.config.interceptor;

import com.sun.istack.internal.NotNull;
import edu.scu.mooddriftbottlerebuild.dao.UsersDao;
import edu.scu.mooddriftbottlerebuild.entity.UsersEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.Set;

@Component
public class BaseInterCeptor implements HandlerInterceptor {

    @Autowired
    UsersDao userDao;

    @Resource
    RedisTemplate<String, String> template;

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws Exception {
        String openid = request.getParameter("openid");
        if(openid == null)return false;
        UsersEntity check = userDao.selectById(openid);
        if(check == null)return false;
        //检查是否在黑名单
        if(template.hasKey(openid + ":forbidden")){
            return false;
        }
        //检查用户是否正常在线
        String pattern = openid + ":use";
        Set<String> keys = template.keys(pattern);
        if(keys == null || keys.isEmpty())return false;
        //将使用次数减一,如果此时使用过多，进入黑名单，使用redis组为黑名单
        ValueOperations<String, String> ops = template.opsForValue();
        String s = ops.get(pattern);
        int t = Integer.parseInt(s);
        if(t == 0){//加入黑名单
            String key = openid + ":forbidden";
            ops.set(key, "1");
            return false;
        }else{//使用次数减1
            Long expire = template.getExpire(pattern);
            t--;
            if(expire == null)expire = -1L;
            if(expire > 0)ops.set(pattern, t+"", Duration.ofSeconds(expire));
        }
        return true;
    }

    @Override
    public void postHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler, ModelAndView modelAndView) throws Exception {
        return;
    }

    @Override
    public void afterCompletion(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}

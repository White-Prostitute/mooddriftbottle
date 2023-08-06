package edu.scu.mooddriftbottlerebuild.config.interceptor;

import com.sun.istack.internal.NotNull;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.InetAddress;
import java.time.Duration;

@Component
public class IpInterceptor implements HandlerInterceptor {

    @Resource
    RedisTemplate<String, String> template;

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws Exception {
        String address = getIpAddress(request);
        //暂时使用redis
        if(template.hasKey(address+":forbidden")){//黑名单用户
            return false;
        }
        //通过的ip对使用频率进行判断
        Boolean hasKey = template.hasKey(address);
        ValueOperations<String, String> ops = template.opsForValue();
        assert hasKey != null;
        if(hasKey){//如果已经有该key
            String s = ops.get(address);
            assert s != null;
            int k = Integer.parseInt(s);
            if(k > 40){//10s大于40次,放入黑名单
                String key = address + ":forbidden";
                ops.set(key, "1");
                return false;
            }else{//请求次数加1
                k++;
                Long expire = template.getExpire(address);
                assert expire != null;
                if(expire > 0)ops.set(address, k+"", Duration.ofSeconds(expire));
            }
        }else{
            ops.set(address, "0", Duration.ofSeconds(10));
        }
        return true;
    }

    //获取客户端IP地址
    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if(ip == null || ip.length() == 0 || "unknow".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length () == 0 || "unknown".equalsIgnoreCase (ip)) {
            ip = request.getHeader ("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length () == 0 || "unknown".equalsIgnoreCase (ip)) {
            ip = request.getRemoteAddr ();
            if (ip.equals ("127.0.0.1")) {
                //根据网卡取本机配置的IP
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost ();
                } catch (Exception e) {
                    e.printStackTrace ();
                }
                ip = inet.getHostAddress ();
            }
        }
        // 多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (ip != null && ip.length () > 15) {
            if (ip.indexOf (",") > 0) {
                ip = ip.substring (0, ip.indexOf (","));
            }
        }
        return ip;
    }
}

package edu.scu.mooddriftbottlerebuild.config;

import edu.scu.mooddriftbottlerebuild.config.interceptor.BaseInterCeptor;
import edu.scu.mooddriftbottlerebuild.config.interceptor.IpInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SpringMvcSupport implements WebMvcConfigurer {

    @Autowired
    BaseInterCeptor baseInterCeptor;

    @Autowired
    IpInterceptor ipInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //ip拦截器
        registry.addInterceptor(ipInterceptor).addPathPatterns("/**");

        //数据库拦截器
        registry.addInterceptor(baseInterCeptor).
                addPathPatterns("/bottle/throw_bottle").
                addPathPatterns("/bottle/remove_bottle").
                addPathPatterns("/reply/reply").
                addPathPatterns("/reply/update_reply").
                addPathPatterns("reply/delete").
                excludePathPatterns("/img/**");
    }

}

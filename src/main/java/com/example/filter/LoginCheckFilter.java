package com.example.filter;

import com.alibaba.fastjson.JSON;
import com.example.common.BaseContext;
import com.example.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Blockbuster
 * @date 2022/4/17 11:00:00 星期日
 * 检查用户是否登录
 */

@Slf4j
@WebFilter(filterName = "LoginCheckFilter", urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    // 路径匹配器 支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        // 获取本次请求的 URL
        String requestURI = request.getRequestURI();
        // 放行 URL
        String[] urls= new String[]{
                "/employee/login",
                "/employee/logout",
                // 需要通配符匹配
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/login",
                "/user/sendMsg"
        };

        // 占位符 {} 语法
        log.info("拦截到{}", requestURI);

        // 判断 URL 是否放行
        boolean check = check(urls, requestURI);
        if (check){
            log.info("本次请求{}不需要处理", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        /**
         * 判断后台是否登录
         */
        if (request.getSession().getAttribute("employee") != null){
            log.info("用户已登录，用户id为：{}", request.getSession().getAttribute("employee"));
            // 获取用户 id 放入当前线程的 ThreadLocal 中
            Long id = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(id);

            filterChain.doFilter(request, response);
            return;
        }

        /**
         * 判断客户是否登录
         */
        if (request.getSession().getAttribute("user") != null){
            log.info("用户已登录，用户id为：{}", request.getSession().getAttribute("user"));
            // 获取用户 id 放入当前线程的 ThreadLocal 中
            Long id = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(id);

            filterChain.doFilter(request, response);
            return;
        }


        log.info("没登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));

    }

    // 路径检查 判断当前路径是否放行
    public boolean check(String[] urls, String requestURI){
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match){
                return true;
            }
        }
        return false;
    }



}

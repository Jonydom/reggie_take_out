package com.itheima.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查用户是否已经完成登录
 */
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    // 路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // 1. 获取本次请求的URI
        String requestURI = request.getRequestURI();
        log.info("拦截到了请求：{}", requestURI);
        // 定义白名单，即不需要处理的请求路径
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",  // 过滤掉上传下载功能
                "/user/sendMsg",  // 移动端发送短信
                "/user/login"  // 移动端登录
        };
        // 2. 判断本次请求是否需要处理
        boolean check = check(urls, requestURI);
        // 3. 如果不需要处理，则直接放行
        if (check) {
            filterChain.doFilter(request, response);
            return;
        }
        // 4-1. 判断PC端后台登录状态，如果已登录，则直接放行
        if (request.getSession().getAttribute("employee") != null) {

            // 设置线程id到ThreadLocal中，方便后面方法调用线程id变量
            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);

            filterChain.doFilter(request, response);
            return;
        }

        // 4-2. 判断移动端登录状态，如果已登录，则直接放行
        if (request.getSession().getAttribute("user") != null) {

            // 设置线程id到ThreadLocal中，方便后面方法调用线程id变量
            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);

            filterChain.doFilter(request, response);
            return;
        }
        // 5. 如果未登录则返回未登录结果，通过输出流方式向客户端页面响应数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    public boolean check(String[] urls, String requestURI) {
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match) {
                return true;
            }
        }
        return false;
    }
}

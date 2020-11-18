/*********************************************************************************
 *                                                                               *
 * The MIT License (MIT)                                                         *
 *                                                                               *
 * Copyright (c) 2015-2020 aoju.org and other contributors.                      *
 *                                                                               *
 * Permission is hereby granted, free of charge, to any person obtaining a copy  *
 * of this software and associated documentation files (the "Software"), to deal *
 * in the Software without restriction, including without limitation the rights  *
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell     *
 * copies of the Software, and to permit persons to whom the Software is         *
 * furnished to do so, subject to the following conditions:                      *
 *                                                                               *
 * The above copyright notice and this permission notice shall be included in    *
 * all copies or substantial portions of the Software.                           *
 *                                                                               *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR    *
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,      *
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE   *
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER        *
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, *
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN     *
 * THE SOFTWARE.                                                                 *
 ********************************************************************************/
package org.aoju.bus.starter.wrapper;

import org.aoju.bus.core.lang.Ansi;
import org.aoju.bus.core.lang.Http;
import org.aoju.bus.core.lang.Normal;
import org.aoju.bus.extra.servlet.ServletKit;
import org.aoju.bus.logger.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 允许定制处理程序执行链的工作流,可以注册任何数量的现有或自定义拦截器
 * 对于某些处理程序组,添加常见的预处理行为不需要修改每个处理程序实现
 *
 * @author Kimi Liu
 * @version 6.1.2
 * @since JDK 1.8+
 */
@Component
public class GenieWrapperHandler implements HandlerInterceptor {

    /**
     * 请求日志信息
     *
     * @param method  请求类型
     * @param request 网络请求
     */
    private static void isHandle(HttpServletRequest request, String method) {
        switch (method) {
            case Http.ALL:
                method = Ansi.BgBlack.and(Ansi.White).format(" %s ", method);
                break;
            case Http.GET:
                method = Ansi.BgGreen.and(Ansi.Black).format(" %s ", method);
                break;
            case Http.POST:
                method = Ansi.BgBlue.and(Ansi.Black).format(" %s ", method);
                break;
            case Http.DELETE:
                method = Ansi.BgRed.and(Ansi.Black).format(" %s ", method);
                break;
            case Http.PUT:
                method = Ansi.BgYellow.and(Ansi.Black).format(" %s ", method);
                break;
            case Http.OPTIONS:
                method = Ansi.BgCyan.and(Ansi.Black).format(" %s ", method);
                break;
            case Http.BEFORE:
                method = Ansi.BgMagenta.and(Ansi.Black).format(" %s ", method);
                break;
            case Http.AFTER:
                method = Ansi.BgWhite.and(Ansi.Black).format(" %s ", method);
                break;
        }
        Logger.info("{} {} {} {}", Ansi.isWindows ? Normal.EMPTY : "==>", ServletKit.getClientIP(request), method, request.getRequestURL().toString());
    }

    /**
     * 业务处理器处理请求之前被调用,对用户的request进行处理,若返回值为true,
     * 则继续调用后续的拦截器和目标方法；若返回值为false, 则终止请求；
     * 这里可以加上登录校验,权限拦截、请求限流等
     *
     * @param request  当前的HTTP请求
     * @param response 当前的HTTP响应
     * @param handler  执行的处理程序
     * @return 如果执行链应该继续执行, 则为:true 否则:false
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        final String method = request.getMethod().toUpperCase();
        isHandle(request, method);
        if (Http.GET.equals(method)
                || Http.POST.equals(method)
                || Http.PATCH.equals(method)
                || Http.PUT.equals(method)) {
            if (request instanceof CacheRequestWrapper) {
                CacheRequestWrapper cacheRequestWrapper = ((CacheRequestWrapper) request);
                Logger.info("==> {}", new String(cacheRequestWrapper.getBody()));
            }
        }
        return true;
    }

    /**
     * 拦截处理程序的执行 实际上是在HandlerAdapter之后调用的
     * 调用处理程序,但在DispatcherServlet呈现视图之前
     * 可以通过给定的ModelAndView向视图公开额外的模型对象
     * DispatcherServlet在一个执行链中处理一个处理程序,由
     * 任意数量的拦截器,处理程序本身在最后
     * 使用这种方法,每个拦截器可以对一个执行进行后处理,
     * 按执行链的相反顺序应用
     *
     * @param request      当前的HTTP请求
     * @param response     当前的HTTP响应
     * @param handler      执行的处理程序
     * @param modelAndView 处理程序返回的{code ModelAndView} 也可以是{@code null})
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
    }

    /**
     * 完成请求处理后回调,将调用处理程序执行的任何结果,
     * 因此允许进行适当的资源清理等
     * 注意:只有在拦截器的{@code preHandle} 方法返回{@code true}
     * 与{@code postHandle}方法一样,将在每个方法上调用该方法,
     * 在链中的拦截器的顺序是相反的,所以第一个拦截器是最后调用的
     *
     * @param request   当前的HTTP请求
     * @param response  当前的HTTP响应
     * @param handler   执行的处理程序
     * @param exception 处理程序执行时抛出异常
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception exception) {
        final String method = request.getMethod();
        if (Http.POST.equals(method)
                || Http.PATCH.equals(method)
                || Http.PUT.equals(method)) {
            if (response instanceof CacheResponseWrapper) {
                CacheResponseWrapper cacheResponseWrapper = ((CacheResponseWrapper) response);
                Logger.info("<== {}", new String(cacheResponseWrapper.getBody()).length());
            }
        }
    }

    /**
     * 启用拦截器
     */
    @Bean
    WebMvcConfigurer enableGenieHandler() {
        return new WebMvcConfigurer() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(new GenieWrapperHandler());
            }
        };
    }

}

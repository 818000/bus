package org.aoju.bus.starter.goalie;

import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import org.aoju.bus.goalie.Athlete;
import org.aoju.bus.goalie.Config;
import org.aoju.bus.goalie.filter.*;
import org.aoju.bus.goalie.handler.ApiRouterHandler;
import org.aoju.bus.goalie.handler.ApiWebMvcRegistrations;
import org.aoju.bus.goalie.handler.GlobalExceptionHandler;
import org.aoju.bus.goalie.metric.Authorize;
import org.aoju.bus.goalie.registry.*;
import org.aoju.bus.spring.SpringBuilder;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.WebExceptionHandler;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebHandler;
import org.springframework.web.server.adapter.WebHttpHandlerBuilder;
import reactor.netty.http.server.HttpServer;

import java.util.ArrayList;
import java.util.List;

/**
 * 路由自动配置
 *
 * @author Kimi Liu
 * @since Java 17++
 */
@ConditionalOnWebApplication
@EnableConfigurationProperties(value = {GoalieProperties.class})
public class GoalieConfiguration implements InitializingBean, DisposableBean {

    @Resource
    GoalieProperties goalieProperties;

    @Resource
    List<WebExceptionHandler> webExceptionHandlers;

    // 保存所有Athlete实例用于销毁
    private final List<Athlete> athletes = new ArrayList<>();

    @ConditionalOnMissingBean
    @Bean
    AssetsRegistry assetsRegistry() {
        return new DefaultAssetsRegistry();
    }

    @ConditionalOnMissingBean
    @Bean
    LimiterRegistry limiterRegistry() {
        return new DefaultLimiterRegistry();
    }

    @Bean
    WebExceptionHandler webExceptionHandler() {
        return new GlobalExceptionHandler();
    }

    @Override
    public void afterPropertiesSet() {
        // 创建所有Athlete实例
        goalieProperties.getServers().forEach(server -> {
            ApiRouterHandler apiRouterHandler = new ApiRouterHandler();

            RouterFunction<ServerResponse> routerFunction = RouterFunctions
                    .route(RequestPredicates.path(server.getPath())
                                    .and(RequestPredicates.accept(MediaType.APPLICATION_FORM_URLENCODED)),
                            apiRouterHandler::handle);

            ServerCodecConfigurer configurer = ServerCodecConfigurer.create();
            configurer.defaultCodecs().maxInMemorySize(Config.MAX_INMEMORY_SIZE);

            // 为每个服务器创建专用的过滤器链
            List<WebFilter> serverWebFilters = new ArrayList<>();
            serverWebFilters.add(new PrimaryFilter(server.getPath()));

            // 根据服务器配置添加解密过滤器
            if (server.getDecrypt().isEnabled()) {
                serverWebFilters.add(new DecryptFilter(server.getDecrypt()));
            }
            // 添加授权过滤器
            serverWebFilters.add(new AuthorizeFilter(server, SpringBuilder.getBean(Authorize.class), SpringBuilder.getBean(AssetsRegistry.class)));

            // 根据服务器配置添加加密过滤器
            if (server.getEncrypt().isEnabled()) {
                serverWebFilters.add(new EncryptFilter(server.getEncrypt()));
            }
            // 根据服务器配置添加限流过滤器
            if (server.getLimit().isEnabled()) {
                serverWebFilters.add(new LimitFilter(SpringBuilder.getBean(LimiterRegistry.class)));
            }
            serverWebFilters.add(new FormatFilter());

            WebHandler webHandler = RouterFunctions.toWebHandler(routerFunction);
            HttpHandler handler = WebHttpHandlerBuilder.webHandler(webHandler)
                    .filters(filters -> filters.addAll(serverWebFilters))
                    .exceptionHandlers(handlers -> handlers.addAll(webExceptionHandlers))
                    .codecConfigurer(configurer)
                    .build();
            ReactorHttpHandlerAdapter adapter = new ReactorHttpHandlerAdapter(handler);
            HttpServer httpServer = HttpServer.create()
                    .port(server.getPort()).handle(adapter);
            athletes.add(new Athlete(server.getName(), httpServer));
        });
        // 手动初始化所有Athlete实例
        athletes.forEach(Athlete::init);
    }

    @Bean
    public WebMvcRegistrations customWebMvcRegistrations() {
        return this.goalieProperties.isCondition() ? null : new ApiWebMvcRegistrations();
    }

    @Override
    public void destroy() {
        athletes.forEach(Athlete::destroy);
    }

}

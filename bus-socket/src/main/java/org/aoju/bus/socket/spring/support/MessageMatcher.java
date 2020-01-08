package org.aoju.bus.socket.spring.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aoju.bus.core.lang.Symbol;
import org.aoju.bus.core.lang.exception.SocketException;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;

import java.io.IOException;
import java.util.Objects;

/**
 * @author Kimi Liu
 * @version 5.5.1
 * @since JDK 1.8+
 */
public class MessageMatcher {

    private PathMatcher matcher = new AntPathMatcher();
    private ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 验证客户端消息的主题和类型
     *
     * @param pattern 与message的destination属性匹配的模板,可以是通配符
     * @param message 客户端消息
     * @param type    消息类型
     * @return {@code true} 验证成功,{@code false} 验证失败
     */
    public boolean matches(String pattern, String type, MessageFrom message) {
        if (!Objects.equals(type, message.getType())) {
            return false;
        }
        String destination = message.getDestination();
        if (pattern == null) {
            pattern = "";
        }
        if (destination == null) {
            destination = "";
        }
        return matcher.match(pattern, destination);
    }

    public void sendMessage(WebSocketSession session, Object message) {
        if (message == null) {
            return;
        }
        String content;
        if (message instanceof String) {
            content = message.toString();
        } else {
            try {
                content = objectMapper.writeValueAsString(message);
            } catch (JsonProcessingException e) {
                throw new SocketException("json转换出错", e);
            }
        }
        ConcurrentWebSocketSessionDecorator decorator = new ConcurrentWebSocketSessionDecorator(session, -1, -1);
        try {
            decorator.sendMessage(new TextMessage(Symbol.SPACE + content));
        } catch (IOException e) {
            throw new SocketException("发送消息出错", e);
        }
    }

}

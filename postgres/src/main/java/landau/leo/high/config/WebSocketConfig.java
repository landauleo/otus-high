package landau.leo.high.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic"); //только на адреса с этим префиксом могут подписываться клиенты
        registry.setApplicationDestinationPrefixes("/app"); //сообщения, адресованные куда-то, начиная с /app, будут маршрутизироваться к методам в приложении, обозначенным для обработки сообщений
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/post-feed") //куда подключаются клиенты
                .withSockJS(); //для обеспечения обратной совместимости с браузерами, которые не поддерживают WebSocket, позволяет использовать альтернативные транспорты для имитации поведения WebSocket
    }

}
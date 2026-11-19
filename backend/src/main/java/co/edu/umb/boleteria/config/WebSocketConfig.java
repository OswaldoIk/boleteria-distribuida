package co.edu.umb.boleteria.config;
import org.springframework.beans.factory.annotation.Value;import org.springframework.context.annotation.Configuration;import org.springframework.messaging.simp.config.MessageBrokerRegistry;import org.springframework.web.socket.config.annotation.*;
@Configuration @EnableWebSocketMessageBroker public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
 @Value("${app.allowed-origin:http://localhost:*}") String origin;
 public void configureMessageBroker(MessageBrokerRegistry c){c.enableSimpleBroker("/topic");c.setApplicationDestinationPrefixes("/app");}
 public void registerStompEndpoints(StompEndpointRegistry r){r.addEndpoint("/ws/aforo").setAllowedOriginPatterns(origin).withSockJS();}
}

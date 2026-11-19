package co.edu.umb.boleteria.service;
import co.edu.umb.boleteria.event.AforoCambiadoEvent;import org.springframework.messaging.simp.SimpMessagingTemplate;import org.springframework.stereotype.Service;import org.springframework.transaction.event.*;
@Service public class WebSocketService {private final SimpMessagingTemplate broker;public WebSocketService(SimpMessagingTemplate b){broker=b;}@TransactionalEventListener(phase=TransactionPhase.AFTER_COMMIT) public void publicar(AforoCambiadoEvent e){broker.convertAndSend("/topic/aforo-events",e);}}

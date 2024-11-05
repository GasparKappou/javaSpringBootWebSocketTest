package com.example.demo.handler;

import com.example.demo.model.ChatMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.util.ArrayList;
import java.util.List;

public class ChatWebSocketHandler extends TextWebSocketHandler {
	private List<WebSocketSession> sessions = new ArrayList<>();
	private ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public void afterConnectionEstablished(WebSocketSession session) {
		sessions.add(session);
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) {
		try {
			// Convierte el mensaje JSON en un objeto ChatMessage
			ChatMessage chatMessage = objectMapper.readValue(message.getPayload(), ChatMessage.class);

			// Convierte el objeto de vuelta a JSON para enviarlo a todos
			String jsonMessage = objectMapper.writeValueAsString(chatMessage);

			for (WebSocketSession webSocketSession : sessions) {
				if (webSocketSession.isOpen()) {
					webSocketSession.sendMessage(new TextMessage(jsonMessage));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				session.close(); // Cierra la sesión en caso de error
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
		sessions.remove(session);
	}
}
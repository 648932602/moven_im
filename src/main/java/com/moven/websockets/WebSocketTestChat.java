package com.moven.websockets;

import java.io.IOException;
import java.util.Set;

import javax.servlet.http.HttpSession;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/chat/{username}")
public class WebSocketTestChat {
	Set<Session> session_list = null;

	@OnMessage
	public void onMessage(String message, Session session) {
		try{
			session_list = session.getOpenSessions();
			HttpSession httpSession = null;
			String username = "";
			if(session instanceof HttpSession){
				httpSession = (HttpSession)session;
				Object param = httpSession.getAttribute("username");
				if(null != param){
					username = param.toString();
				}
			} else {
				username = session.getId();
			}
			for (Session s : session_list) {
				s.getBasicRemote().sendText("当前总人数[" + session_list.size() + "]---" + username + "说:" + message);
			}
		} catch(IOException e){
			System.out.println("Client error");
		}
	}
	
	@OnError
    public void onError(Throwable t) throws Throwable {
		System.out.println("Client error = "+t.getMessage());
    }
	
	@OnOpen
	public void onOpen(@PathParam("username") String username, Session session) {
		HttpSession httpSession = null;
		if(session instanceof HttpSession){
			httpSession = (HttpSession)session;
			httpSession.setAttribute("username", username);
		}
		System.out.println("Client connected");
	}

	@OnClose
	public void onClose() {
		System.out.println("Connection closed");
	}

}

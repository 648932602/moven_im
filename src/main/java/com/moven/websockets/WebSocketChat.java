package com.moven.websockets;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/chat/{username}/{receivername}")
public class WebSocketChat {
	//广播地址列表
//	Set<Session> session_list = null;
	//用户连接池
	static final Map<String, Session> sessionPool = new HashMap<String, Session>();

	/**
	 * Description:广播式方法
	 * @author moshengwei
	 * @date 2017年5月11日 下午4:46:09
	 * @param message	发送的消息
	 * @param session	连接的用户
	 */
//	@OnMessage
//	public void onMessage(String message, Session session) {
//		try{
//			session_list = session.getOpenSessions();
//			HttpSession httpSession = null;
//			String username = "";
//			if(session instanceof HttpSession){
//				httpSession = (HttpSession)session;
//				Object param = httpSession.getAttribute("username");
//				if(null != param){
//					username = param.toString();
//				}
//			} else {
//				username = session.getId();
//			}
//			for (Session s : session_list) {
//				s.getBasicRemote().sendText("当前总人数[" + session_list.size() + "]---" + username + "说:" + message);
//			}
//		} catch(IOException e){
//			System.out.println("Client error");
//		}
//	}
	
	/**
	 * Description:点对点发送
	 * @author moshengwei
	 * @date 2017年5月11日 下午4:46:42
	 * @param message	发送的消息
	 * @param session	连接的用户
	 */
	@OnMessage
	public void onMessage(String message, Session session) {
		try{
			Map<String, String> params = session.getPathParameters();
			String username = params.get("username");
			String receivername = params.get("receivername");
			Session receiver = sessionPool.get(receivername);
			if(null != receiver){
				receiver.getBasicRemote().sendText("["+username + "] 对 [" + receivername + "] 说:" + message);
				session.getBasicRemote().sendText("["+username + "] 对 [" + receivername + "] 说:" + message);
			} else {
				session.getBasicRemote().sendText("["+receivername+"] 未连接到服务器");
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
		if(null != session){
			sessionPool.put(username, session);
		}
		System.out.println("Client connected! username : "+username);
	}

	@OnClose
	public void onClose(@PathParam("username") String username, Session session) {
		if(null != session){
			sessionPool.remove(username);
		}
		System.out.println("Connection closed! username : "+username);
	}

}

package client.websocket;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.Map;


public class ClientSocket extends WebSocketClient {
	private String data;

	public ClientSocket(URI serverURI) {
		super(serverURI);
	}

	public ClientSocket(URI serverURI, Map<String, String> httpHeaders) {
		super(serverURI, httpHeaders);
	}

	@Override
	public void onError(Exception arg0) {
		System.out.println("error...");
	}

	@Override
	public void onOpen(ServerHandshake serverHandshake) {
		System.out.println("====================>打开链接");
	}

	@Override
	public void onMessage(String s) {
		synchronized (this) {
			this.data = s;
		}

	}

	@Override
	public void onClose(int i, String s, boolean b) {
		System.out.println("====================>链接已关闭");
	}

	public String getData() {
		return this.data;
	}

}

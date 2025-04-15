package client.websocket;

import org.java_websocket.enums.ReadyState;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;


public class ClientManager {
	private ClientSocket socket;

	public void Connect(String url, Map<String, String> httpHeaders){
		try {
			socket=new ClientSocket(new URI(url), httpHeaders);
			socket.connect();
		} catch (URISyntaxException e) {
			e.printStackTrace();
			System.out.println("Connect error...");
		}
	}

	public void stop(){
		System.out.println("关闭连接");
		socket.close();
	}

	public void send(String message){
		try {
			while (!socket.getReadyState().equals(ReadyState.OPEN)) {
				System.out.println("连接中···请稍后");
				Thread.sleep(1000);
			}
			socket.send(message);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public String receive() {
//		System.out.println("接收到的消息:"+ socket.getData());
		return socket.getData();
	}

	public static void main(String[] args) {
//		Map<String, String> httpHeaders = new HashMap<>();
//		httpHeaders.put("Cookie", "_sw_token=9CC45AB6C0CFD3F9662AC3FF6F4BEB3D");
//		httpHeaders.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/537.36 (KHTML, like Gecko) " +
//				"Chrome/80.0.3987.163 Safari/537.36");
//		ClientManager client = new ClientManager();
//		client.Connect("ws://test.magic.shuwen.com/webSocket", httpHeaders);
//		client.send("full");
//		System.out.println(client.receive());
//		while (true) {
//			if(client.receive().isEmpty()) {
//				client.stop();
//				break;
//			}
//			//超时回调
//			System.out.println("获取回调数据中...");
//		}
	}
}

package net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import global.Configuration;

public class SocketClient {
	/**
	 * start函数进行报文交换
	 * @param str - 传送的报文
	 */
	public static void start(String str) {
		while (true) {
			Socket socket = null;
			try {
				socket = new Socket(Configuration.IP, Configuration.PORT);
				DataInputStream input = new DataInputStream(socket.getInputStream());
				DataOutputStream output = new DataOutputStream(socket.getOutputStream());
				output.writeUTF(str);
				String ret = input.readUTF();
				System.out.println("服务端返回"+ret);
				if ("OK".equals(ret)) {
					System.out.println("客户端即将关闭");
					Thread.sleep(5000);
					break;
				}
				output.close();
				input.close();
			} catch(Exception e) {
				System.out.println("客户端异常：" + e.getMessage());
			} finally {
				if (socket != null) {
					try {
						socket.close();
					} catch (IOException e) {
						socket = null;
						System.out.println("客户端finally异常" + e.getMessage());
					}
				}
			}
		}
	}
}

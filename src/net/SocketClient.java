package net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import global.Configuration;

public class SocketClient {
	/**
	 * start�������б��Ľ���
	 * @param str - ���͵ı���
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
				System.out.println("����˷���"+ret);
				if ("OK".equals(ret)) {
					System.out.println("�ͻ��˼����ر�");
					Thread.sleep(5000);
					break;
				}
				output.close();
				input.close();
			} catch(Exception e) {
				System.out.println("�ͻ����쳣��" + e.getMessage());
			} finally {
				if (socket != null) {
					try {
						socket.close();
					} catch (IOException e) {
						socket = null;
						System.out.println("�ͻ���finally�쳣" + e.getMessage());
					}
				}
			}
		}
	}
}

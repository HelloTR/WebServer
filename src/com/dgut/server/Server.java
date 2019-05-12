package com.dgut.server;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * 有窗体的web服务器
 * @author zhr
 *
 */
public class Server extends JFrame {

	private static final long serialVersionUID = 1L;
	// 窗体的宽高
	public int width = 600;
	public int height = 400;
	// 开启服务器按钮
	private JButton btnOpenServer;
	// 显示文本框
	private JTextArea textArea;
	// 格式化时间
	private SimpleDateFormat format = new SimpleDateFormat(
			"yyyy年MM月dd日 HH:mm:ss");
	// 服务器端口号
	private static final int PORT = 8888;

	public Server() {
		// 设置窗体标题
		setTitle("服务器");
		// 设置大小
		setSize(width, height);
		// 设置不可以重置大小
		setResizable(false);
		// 设置显示在中心
		setLocationRelativeTo(null);
		// 设置点击x关闭
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// 初始化按钮
		btnOpenServer = new JButton("开启服务器");
		// 初始化显示文本框
		textArea = new JTextArea();
		// 设置不可以编辑
		textArea.setEditable(false);
		// 设置文本框滚动条
		JScrollPane scroll = new JScrollPane(textArea);
		// 垂直和水平都出现滚动条
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		// 加入到窗体中
		add(btnOpenServer, BorderLayout.NORTH);
		add(scroll, BorderLayout.CENTER);
		// 开启服务器按钮的点击事件
		btnOpenServer.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// 当开启服务后把按钮设置为不可点击
				btnOpenServer.setEnabled(false);
				// 开启一个线程去开启服务
				new Thread() {
					@SuppressWarnings("resource")
					public void run() {
						try {
							// 在8888端口监听
							ServerSocket serverSocket = new ServerSocket(PORT);
							while (true) {
								// 当有套接字到来时，开启一个线程去处理
								Socket socket = serverSocket.accept();
								new SocketThread(socket).start();
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}.start();
			}
		});
	}

	private class SocketThread extends Thread {
		private Socket socket;

		public SocketThread(Socket socket) {
			this.socket = socket;
		}

		@Override
		public void run() {
			try {
				StringBuffer sb = new StringBuffer();
				// 获取访问者的IP地址
				sb.append("用户IP：").append(
						socket.getInetAddress().getHostAddress());
				// 格式化访问时间
				sb.append("\n访问时间：").append(format.format(new Date()))
						.append("\n头部信息\n");
				// 获取输入流
				InputStream in = socket.getInputStream();
				byte b[] = new byte[1024];
				int len = in.read(b);
				// 获取头部信息
				String head = new String(b, 0, len);
				// 获取用户要访问的文件路径
				String filePath = head.substring(head.indexOf("/") + 1,
						head.indexOf("HTTP")).trim();
				System.out.println("文件路径:" + filePath);
				File file = new File(filePath);
				sb.append(head);
				OutputStream out = socket.getOutputStream();
				BufferedWriter writer = new BufferedWriter(
						new OutputStreamWriter(out));
				// 如果文件存在
				if (file.exists() && file.isFile()) {
					if (filePath.endsWith("txt") || filePath.endsWith("html")) {
						writer.write("HTTP/1.1 200 OK");
						writer.newLine();
						if (filePath.endsWith("txt")) {
							writer.write("Content-Type: text/plain");
							writer.newLine();
						} else if (filePath.endsWith("html")) {
							writer.write("Content-Type: text/html");
							writer.newLine();
						}
						writer.write("Content-Length: " + file.length());
						writer.newLine();
						writer.newLine();
						writer.flush();
						sendTextFile(writer, file);
					} else {
						writer.write("HTTP/1.1 200 OK");
						writer.newLine();
						if (filePath.endsWith("jpg")) {
							writer.write("Content-Type: image/jpeg");
							writer.newLine();
						} else if (filePath.endsWith("png")) {
							writer.write("Content-Type: image/png");
							writer.newLine();
						} else if (filePath.endsWith("mp3")) {
							writer.write("Content-Type: audio/mp3");
							writer.newLine();
						} else if (filePath.endsWith("mp4")) {
							// writer.write("Content-Type: vedio/mp4");
							writer.write("Content-Type: audio/mp4");
							writer.newLine();
						}
						writer.write("Content-Length: " + file.length());
						writer.newLine();
						writer.newLine();
						writer.flush();
						sendImageFile(out, file);
					}
				} else {
					writer.write("HTTP/1.1 404 Not Found");
					writer.newLine();
					writer.flush();
					sb.append("文件 " + filePath + " 不存在\n");
				}
				out.flush();
				out.close();
				writer.close();
				socket.close();
				sb.append("===============================================================\n\n");
				textArea.append(sb.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private void sendImageFile(OutputStream out, File file) {
			try {
				InputStream in = new FileInputStream(file);
				byte b[] = new byte[1024 * 1024];
				int len = -1;
				while ((len = in.read(b)) != -1) {
					System.out.println(len);
					out.write(b, 0, len);
				}
				in.close();
			} catch (Exception e) {

			}

		}

		/**
		 * 发送文件
		 * 
		 * @param out
		 * @param file
		 */
		private void sendTextFile(BufferedWriter writer, File file) {
			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(new FileInputStream(file), "gbk"));
				for (String line = reader.readLine(); line != null; line = reader
						.readLine()) {
					System.out.println(line);
					writer.write(line);
					writer.newLine();
					writer.flush();
				}
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		Server server = new Server();
		server.setVisible(true);
	}

}

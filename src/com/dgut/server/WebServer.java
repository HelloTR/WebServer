package com.dgut.server;
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

/**
 * 无窗体web服务器
 * @author zhr
 *
 */
public class WebServer {
	public static void main(String[] args) throws Exception {
		ServerSocket welcomeSocket = new ServerSocket(8888);
		while (true) {
			// 创建连接套接字
			Socket socket = welcomeSocket.accept();
			try {
				InputStream in = socket.getInputStream();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(in));
				String path = "";
				for (String line = reader.readLine(); line != null; line = reader
						.readLine()) {
					if (line.startsWith("GET") || line.startsWith("get")) {
						int start = line.indexOf("/") + 1;
						int end = line.lastIndexOf(" ");
						path = line.substring(start, end);
						break;
					}
				}
				System.out.println("文件路径:" + path);
				File file = new File(path);
				OutputStream out = socket.getOutputStream();
				BufferedWriter writer = new BufferedWriter(
						new OutputStreamWriter(out));
				// 如果存在这样的文件
				if (file.exists() && file.isFile()) {
					if (path.endsWith("txt") || path.endsWith("html")) {
						writer.write("HTTP/1.1 200 OK");
						writer.newLine();
						if (path.endsWith("txt")) {
							writer.write("Content-Type: text/plain");
							writer.newLine();
						} else if (path.endsWith("html")) {
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
						if (path.endsWith("jpg")) {
							writer.write("Content-Type: image/jpeg");
							writer.newLine();
						}else if(path.endsWith("png")){
							writer.write("Content-Type: image/png");
							writer.newLine();
						}else if(path.endsWith("mp3")){
							writer.write("Content-Type: audio/mp3");
							writer.newLine();
						}else if(path.endsWith("mp4")){
//							writer.write("Content-Type: vedio/mp4");
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
					System.out.println("不存在");
					writer.write("HTTP/1.1 404 Not Found");
					writer.newLine();
					writer.flush();
				}
				writer.close();
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void sendImageFile(OutputStream out, File file) {
		try {
			InputStream in = new FileInputStream(file);
			byte b[] = new byte[1024*1024];
			int len = -1;
			while ((len = in.read(b)) != -1) {
				System.out.println(len);
				out.write(b,0,len);
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
	private static void sendTextFile(BufferedWriter writer, File file) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(file), "gbk"));
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

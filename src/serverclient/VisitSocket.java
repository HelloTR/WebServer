package serverclient;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JTextArea;

public class VisitSocket extends Thread {

	Socket socket;
	JTextArea textArea;

	public VisitSocket(Socket socket, JTextArea textArea) {
		this.socket = socket;
		this.textArea = textArea;
	}

	@SuppressWarnings("deprecation")
	public void run() {
		try {
			DataInputStream instream = new DataInputStream(
					socket.getInputStream());
			String inline = instream.readLine();
			System.out.println(inline);
			// 报文推送
			InputStream in = socket.getInputStream();
			byte[] buf = new byte[1024];
			int len = in.read(buf);
			// System.out.println(new String(buf, 0, len));

			PrintWriter outPrintWriter = new PrintWriter(
					socket.getOutputStream(), true);
			if (getrequest(inline)) {
				String filename = getfilename(inline);
				System.out.println(filename);
				File file = new File(filename);
				System.out.println(file.getAbsolutePath());
				System.out.println(file.getPath());
				if (file.exists()) {
					System.out.println("存在");
					outPrintWriter.println("HTTP/1.1  200 OK ");
					outPrintWriter.println("Content_Type: text/html");
					outPrintWriter.println("");
					sendfile(outPrintWriter, file);

					// byte [] buf=new byte [1024];
					// int len =in.read(buf);
					textArea.append(new String(buf, 0, len));
					//
					socket.close();
				} else {
					System.out.println("不存在");
					outPrintWriter.println("HTTP/1.1 404");
					outPrintWriter.println("Content_Type: text/html");
					outPrintWriter.println("");
					outPrintWriter.write(" 404 Not Found!");
					outPrintWriter.close();
					socket.close();
				}

			}
		} catch (Exception e) {

		}

	}

	boolean getrequest(String string)// 获取请求类型是否有get
	{
		if (string.length() > 0) {
			if (string.substring(0, 3).equalsIgnoreCase("GET")) {
				return true;
			}
		}
		return false;
	}

	String getfilename(String s)// 访问文件
	{
		String xxString = s.substring(s.indexOf(' ') + 1);
		xxString = xxString.substring(1, xxString.indexOf(' '));
		try {
			if (xxString.charAt(0) == '/') {
				xxString.substring(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (xxString.equals("")) {
			xxString = "index.html";
		}

		return xxString;
	}

	void sendfile(PrintWriter out, File file)// 发送文件file
	{
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String str;
			while ((str = br.readLine()) != null) {
				out.write(str);
			}
			br.close();
			out.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}

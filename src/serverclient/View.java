package serverclient;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

public class View extends JFrame {

	
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextArea textArea;
	ServerSocket serverSocket;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					View frame = new View();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	/**
	 * Create the frame.
	 */
	public View() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		JButton btnNewButton = new JButton("连接到服务器");
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (serverSocket == null) {
					new Thread() {
						public void run() {
							try {
								serverSocket = new ServerSocket(12345);
								while (true) {
									Socket socket = serverSocket.accept();
									new VisitSocket(socket, textArea).start();
									// JOptionPane .showMessageDialog(null,
									// "有客户连接到服务器！");
									// serverSocket.close();
								}

							} catch (Exception e) {
								// TODO: handle exception
								e.printStackTrace();
							}
						}
					}.start();
				}
			}
		});
		contentPane.add(btnNewButton, BorderLayout.NORTH);

		JScrollPane scrollPane = new JScrollPane();
		contentPane.add(scrollPane, BorderLayout.CENTER);

		textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
	}

	// public String textAreanew(byte[] buf, int i, int len) {
	// // TODO Auto-generated method stub
	// String string = new String(buf, i, len);
	// return string;
	// }

}

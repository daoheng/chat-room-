import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class ChatClient extends Frame {

	Socket s=null;
	DataOutputStream dos=null;
	DataInputStream dis=null;
	private boolean bConnected=false;
	
	TextField tfTxt = new TextField();
	TextArea taContent = new TextArea();
	
	Thread tRecv=new Thread(new RecvServer());

	public static void main(String[] args) {
		new ChatClient().launchFrame();
	}

	public void launchFrame() {
		this.setLocation(400, 100);
		this.setSize(500, 400);
		this.add(tfTxt, BorderLayout.SOUTH);
		this.add(taContent, BorderLayout.NORTH);
		pack();
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent arg0) {
				disconnect();
				System.exit(0);
			}
		});
		this.setVisible(true);
		tfTxt.addActionListener(new TFListener());
		//this is the hard point,linked listener part to main 
		this.connect();
		
		tRecv.start();//小心这一句，不理解
	}
	
	/*
	 * start all the issues when connected
	 */
	public void connect() {
		try {
			s = new Socket("127.0.0.1",8888);
			dos=new DataOutputStream(s.getOutputStream());//DOS
			dis=new DataInputStream(s.getInputStream());
System.out.println("connected!");
			bConnected=true;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * end all of the issues when disconnected
	 */
	public void disconnect(){
		try {
			dos.close();
			dis.close();
			s.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		/*
		try {
			bConnected=false;
			tRecv.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			try {
				dos.close();
				dis.close();
				s.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		*/
	}
	
	/*
	 * this is a main function of part of main class, listener part
	 */
	private class TFListener implements ActionListener{

		public void actionPerformed(ActionEvent e) {
			String str = tfTxt.getText().trim();
			//taContent.setText(str);
			tfTxt.setText("");
			
			try {
				dos.writeUTF(str);
				dos.flush();
				//dos.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	private class RecvServer implements Runnable{

		public void run() {
				try {
					while(bConnected){
						String str = dis.readUTF();
						taContent.setText(taContent.getText()+str+'\n');
					}
				} catch (SocketException e) {
					System.out.println("closing byebye!");
				} catch (EOFException e) {
					System.out.println("closing bye");
				} catch (IOException e) {
					e.printStackTrace();
				}
			
		}
	}
		
}

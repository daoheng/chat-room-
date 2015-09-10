import java.net.*;
import java.util.*;
import java.io.*;

public class ChatServer {
	boolean started=false;
	ServerSocket ss=null;
	List<Client> Clients=new ArrayList<Client>();
	
	/*
	 * this is a very good format, this is what the main function should do , should be;
	 */
	public static void main(String[] args) {
		new ChatServer().start();
	}
	 
	public void start(){
		try{
			ss=new ServerSocket(8888);
			started=true;
		} catch (BindException e){
			System.out.println("port occupied!!!");
			System.out.println("please close related application and restart the server");
			System.exit(0);
		} catch (IOException e){
			e.printStackTrace();
		}
		/*
		 * try/catch is for deal with the exception,some statement need trycatch,
		 * the implement of these statement is not independent, dependent on others,
		 * others is not stable, others is not reliable.so there are possible exceptions.
		 * try/catch is a very easy statement, easy to understand.
		 */
		
		try{
			
			while(started){
				Socket s=ss.accept();//block function needs circulation to keep block
				Client c=new Client(s);
System.out.println("a client connected!");
				new Thread(c).start();//dynamic thread run
				Clients.add(c);
				//dis.close();
			}
		}  	catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				ss.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	/*
	 * the try/catch statement looks hard, but when you try it, actually it is so easy.
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	
	
    class Client implements Runnable{

    	private Socket s;
    	private DataInputStream dis=null;
    	private DataOutputStream dos=null;
    	boolean connected=false;
    	
    	public Client(Socket s){
    		try {
    			this.s=s;
				dis=new DataInputStream(s.getInputStream());
				dos=new DataOutputStream(s.getOutputStream());
				connected=true;
			} catch (IOException e) {
				e.printStackTrace();
			}
    		
    	}
    	
    	public void send(String str) {
				try {
					dos.writeUTF(str);
				} catch (IOException e) {
					Clients.remove(this);
					System.out.println("the client quit,so remove from the client send list");
					//e.printStackTrace();
				}
    	}
    	
		public void run() {
			//String str =null;\
			
			try {//you should put a block into try{}
				while(connected){//a bug have happened here
					String str = dis.readUTF();//zu se xing han shu
System.out.println(str);
					for(int i=0;i<Clients.size();i++){
						Client c=Clients.get(i);
						c.send(str);
					}
				}
			} catch (EOFException e) {
					//e.printStackTrace();
					System.out.println("client closed");
			} catch (IOException e) {
					e.printStackTrace();
			} finally {
					try {
						if(s!=null) s.close();
						if(dis!=null) dis.close();
						if(dos!=null) dos.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				
				}
			}
		}
		
	}


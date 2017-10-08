package ergasia1;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Client implements Runnable {
	private static Socket socket; 
	private static final int REQPERUSER=300;
	private static final int USERS=10;
	private static int id;
	private String request="";
	private String response= "";
	public static final int USER=1;
	private static int port;
	private static String ip;
	
	
		public Client(int id) {
			this.id=id;
			this.request="";
			this.response="";
		}
		
		public void run() {
			try {
				
				Socket socket= new Socket(ip,port);
				BufferedReader in= new BufferedReader(new InputStreamReader(socket.getInputStream()));
				DataOutputStream out= new DataOutputStream(socket.getOutputStream());
				this.request= "HELLO" + socket.getLocalAddress() + "" + socket.getLocalPort() + "" + this.id;
				for(int i=0; i < REQPERUSER; i++) {
					
					long start_time= System.nanoTime();
					out.writeBytes(request);
					response=in.readLine();
					
					long end_time= System.nanoTime();
					
					String tokens []= response.split(" ");
						System.out.println("[" + new Date() + "] Message from server:" + tokens[0] + "" + tokens[1] + "Payload" + (tokens[2].length()*2)/1024 + "kb");
					
						
					long total= end_time - start_time;
					long sum=0;
					sum += total;
					
				}
				
				socket.close();
			}	
				catch(Exception e) {
					e.printStackTrace();
				}
			
			}
		

	public static void main(String [] argv) throws Exception{
		
		try {
			
			ip=argv[0];
			port=Integer.parseInt(argv[1]);

		} catch(Exception e) {
			System.err.println("Cannot connect.");
			System.exit(1);
		}
		
		for(int i=0;i <USER; i++) {
			(new Thread(new Client(i))).start();
		}
		
		
	}		
}

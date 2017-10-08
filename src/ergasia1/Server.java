package ergasia1;
import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.Date;
import java.util.Random;
import java.util.Arrays;

public class Server {
	
	private static int port;
	private static int message;
	private static int repetitions;
	public static int maxRepetitions;
	public static long start_time;
	
	
	private static class TCPWorker implements Runnable {
		
		private Socket client;
		private String clientBuff;
	
	
	public TCPWorker (Socket client) {
		
		this.client=client;
		this.clientBuff="";
	}
	
	public void run() { 
		
		try {
			
			while(this.client.isConnected()) {
				repetitions++;
				BufferedReader reader= new BufferedReader(new InputStreamReader(this.client.getInputStream()));
				DataOutputStream out= new DataOutputStream(this.client.getOutputStream());
				
				this.clientBuff=reader.readLine();
				if(message==0) 
					start_time=System.nanoTime();
				
				if(this.clientBuff.equals("CLOSED")){
					out.writeBytes("Connection Terminated\n");
					client.close();
					
			repetitions--;
			
				break;
				
				}
				
			if(repetitions>maxRepetitions) {
				out.writeBytes("Terminate\n");
				
				break;
			}
			String [] tokens= this.clientBuff.split(" ");
			System.out.println("[" + new Date() + "] Received: "+ this.clientBuff);
			
			Random random=new Random(System.nanoTime());
			int ran= random.nextInt(2000-300);
			int rand= ran+300;
			rand*=1024;
			
			String payload= createPayload(rand);
			
			String response= "Welcome" + tokens[3] + "" + payload;
			out.writeBytes(response + System.nanoTime());
			message+=1;
			
			if(message==repetitions) {
				long end_time= System.nanoTime();
				long total=end_time-start_time;
				System.out.println("Total time:" +total);
				
			}
			
			out.flush();
			
			}
			
		}catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
	


private static String createPayload(int size) {
	char[] payload= new char[size/2];
	Arrays.fill(payload, 'a');
	String str= new String(payload);
	return str;
}


public static ExecutorService TCP_WORKER_SERVICE= Executors.newFixedThreadPool(10);


public static void main(String [] argv) {
	
	message=0;
	
	try {
		
		repetitions=Integer.parseInt(argv[1]);
		port= Integer.parseInt(argv[0]);
	}
	catch(Exception e) {
		System.out.println("Wrong input.");
		System.exit(1);
	}
	
	try {
		ServerSocket socket= new ServerSocket(port);
		
		System.out.println("Server listening to" +socket.getInetAddress() 
		+ ":" + socket.getLocalPort());
		
		while(true) {
			Socket client= socket.accept();
			
			TCP_WORKER_SERVICE.submit(new TCPWorker(client));
		}
	} catch(IOException e) {
		e.printStackTrace();
	}
  }
}



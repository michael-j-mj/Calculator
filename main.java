import java.net.ServerSocket;
import java.io.IOException;
import java.net.Socket;
import org.json.JSONObject;


public class main {
	public static void main(String[] args)
	{
		
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(8080);
			while (true) { 
			Socket clientSocket = serverSocket.accept(); 
			new RequestProcessor(clientSocket).run();
			} } catch (IOException e) { 
			    System.out.println("Server exception: " + e.getMessage());
			} 
		} 
	}
package Utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.jar.JarException;

public class socketConnection {
	
	private Socket socket = null;
	private String serverAddress = rp.getPropertyValues("IP").trim();
	private static readProperties rp = new readProperties();
	
	public void connectSocket(int serverPort) throws UnknownHostException, IOException {
		this.socket = new Socket(this.serverAddress, serverPort);			
	}
	
	
	public void sendRequestToAESDK(String data) throws UnknownHostException, IOException, InterruptedException {
		OutputStream outputStream = this.socket.getOutputStream();
		PrintWriter out = new PrintWriter(outputStream, true);
		out.println(data);
	}

	public String receiveResponseFromAESDK() throws IOException, InterruptedException, JarException {
		InputStream inputStream = this.socket.getInputStream();
		BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
		String response = in.readLine();
		return response;
		}
	
	public void closeSocket() {
		try {
			this.socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
		socketConnection sc = new socketConnection();
		int serverPort = Integer.parseInt(rp.getPropertyValues("Outdoor_Port").trim());
		String data = "{\"GetCardBINRequest\":{\"CCTID\":\"\",\"ADSDKSpecVer\":\"6.14.9\",\"ClerkID\":\"001009352\",\"POSID\":\"000001\",\"APPID\":\"01\",\"CardDataInfo\":{\"P2PEIV0\":\"\",\"P2PEKSN\":\"\",\"PinBlockMode\":\"\",\"EMVDetailsData\":\"\",\"CardDataSource\":\"4\",\"TokenizedTrackData\":\"\",\"PINBlock\":\"\",\"KSNBlock\":\"\",\"EncryptionMode\":\"00\",\"TrackData\":\"XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX\"},\"LookUpFlag\":\"4\",\"CashBackFlag\":\"0\",\"HeaderMessage\":\"\",\"EntrySource\":\"\",\"StoreLocationID\":\"1\",\"CVVFlag\":\"0\",\"AllowKeyedEntry\":\"N\",\"SessionId\":\"\"}}";
		System.out.println(serverPort);
		for(int i = 1; i <=3 ; i++) {
			sc.connectSocket(serverPort);
			sc.sendRequestToAESDK(data);
			sc.receiveResponseFromAESDK();
			//sc.closeSocket();
		}
		
	}
}



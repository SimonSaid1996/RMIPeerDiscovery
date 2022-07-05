package Distribution;
import Core.RepException;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.sql.SQLOutput;

public class ServerPDPExample {
	DatagramSocket dSocket = null;
	private int udpPort = 6231;
	private int rmiPort;

	private String remoteId;
	public void start(int port,String repid) {
	new Thread(new Runnable() {
		@Override
		public void run(){
			rmiPort = port;
			remoteId= repid;
			runSdu();
			dSocket.close();
			}
		}).start();
	}
	private void runSdu() {
		try {
			byte[] receiveBuff = new byte[1024]; //receiving buffer
			dSocket = new DatagramSocket(udpPort);
			dSocket.setBroadcast(true);
			DatagramPacket dPacket = new DatagramPacket(receiveBuff, receiveBuff.length);
			System.out.println("\nStarted UDP server, listening on Broadcast IP, port "+udpPort);
			while (true) {
				System.out.println("> Ready to receive b-cast packets...");
				dSocket.receive(dPacket); //receiving data
				////////////


				System.out.println("> Received packet from " + dPacket.getAddress().getHostAddress() 
					+ ":" + dPacket.getPort());
				String msg = new String(dPacket.getData(), dPacket.getOffset(), dPacket.getLength());
				String[] splited = msg.split(" ");
				for(int i=1;i<splited.length;i++)

				if (splited[0].equals("PEER_REQUEST")) {
					if(remoteId.equals(splited[1])){
						String srvResponse = ("PEER_RESPONSE "+rmiPort);

						System.out.println(srvResponse);
						byte[] sendBuff = srvResponse.getBytes();
						DatagramPacket dPacket2 = new DatagramPacket(sendBuff, sendBuff.length, dPacket.getAddress(), dPacket.getPort());
						dSocket.send(dPacket2); 	//Send a response
						System.out.println(getClass().getName() + "> Sent response to client IP: "
								+ dPacket.getAddress().getHostAddress() + ":" + dPacket.getPort());
					}
					if(splited[1].equals("listing")){
						String srvResponse = ("PEER_List "+rmiPort+" "+remoteId);

						System.out.println(srvResponse);
						byte[] sendBuff = srvResponse.getBytes();
						DatagramPacket dPacket2 = new DatagramPacket(sendBuff, sendBuff.length, dPacket.getAddress(), dPacket.getPort());
						dSocket.send(dPacket2); 	//Send a response
						System.out.println(getClass().getName() + "> Sent response to client IP: "
								+ dPacket.getAddress().getHostAddress() + ":" + dPacket.getPort());
					}

				}
				Thread.sleep(6000);
			}
		} catch (Exception e) {
			System.out.println("testing");
			System.out.println(new RepException(e).getErrormsg());
		}
	}
  }

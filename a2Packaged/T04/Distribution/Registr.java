package Distribution;
import Core.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class Registr implements IRegistry {
    //might need a map to keep track of the repids, and the registries
    Map<String, Integer> port = new HashMap<>();
    //key is the string, value is the IRepository
    public Registr(int port,String id){

        ServerPDPExample spdp = new ServerPDPExample();
        spdp.start(port,id);

    }
    @Override
    public IRepository find(String id) {
        DatagramSocket dSocket;
        IRepository respond =null;
        int res_counter=0;
        try {
            //Open a random port to send the package
            dSocket = new DatagramSocket();
            dSocket.setSoTimeout(1000);
            dSocket.setBroadcast(true);
            byte[] sendData = ("PEER_REQUEST "+id).getBytes();
            // Broadcast the message over all the network interfaces
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                if (networkInterface.isLoopback() || !networkInterface.isUp()) { continue; } // Omit loopbacks
                for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                    InetAddress broadcast = interfaceAddress.getBroadcast();
                    if (broadcast == null) { continue; } //Don't send if no broadcast IP.
                    try {
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcast, 6231);
                        dSocket.send(sendPacket); // Send the broadcast package!
                    }
                    catch (Exception e) {
                        System.out.println("Regester: problrm whie sending udp request");
                    }
                    System.out.println("\n> Request sent to IP: "
                            + broadcast.getHostAddress() + "; Interface: " + networkInterface.getDisplayName());
                }
            }

            System.out.println("\n> Done looping through all interfaces. Now waiting for a reply!");


            byte[] recvBuf = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);

            while(true) {

                dSocket.receive(receivePacket); //Wait for a response
                //We have a response
                System.out.println("\n> Received packet from " + receivePacket.getAddress().getHostAddress() + " : " + receivePacket.getPort());
                String msg = new String(receivePacket.getData()).trim();
                String[] splited = msg.split(" ");
                if (splited[0].equals("PEER_RESPONSE")) {
                    if(res_counter>1){
                        throw new SocketTimeoutException();
                    }
                    res_counter+=1;
                    int res_port=Integer.valueOf(splited[1]);
                    String res_add=receivePacket.getAddress().getHostAddress();
                    System.out.println("\n> recieved respond: address= "+res_add+" , port= "+res_port);
                    try {
//                        Registry registry = LocateRegistry.getRegistry(res_add, res_port);
//                        respond= (IDistributedRepository) registry.lookup(id);
                        respond=new Connector(id,res_add,res_port).getRepo();
                        System.out.println("trying to find id: "+id+" address: "+res_add+" port"+res_port);
                        if (respond!=null){
                            System.out.println("reg: got repo");
                        }
                        System.out.println("out from find");
                        return respond;
                    } catch (Exception e) {
                        System.err.println("Client exception: " + e.toString());
                        e.printStackTrace();
                    }
                }

			dSocket.close();  //Close the port!
            }

        }
        catch (SocketTimeoutException e) {
            if(res_counter!=1){
                throw new RepException(e);
                //res = "SERVER: ERR Non-existence or ambiguous repository " + keyParts[0];
            }
            return respond;
        }
        catch (IOException ex) {   //print out ioexception
            System.out.println(new RepException(ex).getErrormsg());
        }finally {
            return respond;
        }
    }

    @Override
    public String[] list() {
        DatagramSocket dSocket;
        ArrayList<String> respond =new ArrayList<String>();
        try {
            //Open a random port to send the package
            dSocket = new DatagramSocket();
            dSocket.setSoTimeout(5000);
            dSocket.setBroadcast(true);
            byte[] sendData = ("PEER_REQUEST listing").getBytes();
            // Broadcast the message over all the network interfaces
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                if (networkInterface.isLoopback() || !networkInterface.isUp()) { continue; } // Omit loopbacks
                for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                    InetAddress broadcast = interfaceAddress.getBroadcast();
                    if (broadcast == null) { continue; } //Don't send if no broadcast IP.
                    try {
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcast, 6231);
                        dSocket.send(sendPacket); // Send the broadcast package!
                    }
                    catch (Exception e) {
                        System.out.println("Regester: problrm whie sending udp request");
                    }
                    System.out.println("\n> Request sent to IP: "
                            + broadcast.getHostAddress() + "; Interface: " + networkInterface.getDisplayName());
                }
            }

            System.out.println("\n> Done looping through all interfaces. Now waiting for a reply!");


            byte[] recvBuf = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);

            while(true) {

                dSocket.receive(receivePacket); //Wait for a response
                //We have a response
                System.out.println("\n> Received packet from " + receivePacket.getAddress().getHostAddress() + " : " + receivePacket.getPort());
                String msg = new String(receivePacket.getData()).trim();
                System.out.println("msg="+msg);
                String[] splited = msg.split(" ");
                if (splited[0].equals("PEER_List")) {
                    String res_port=splited[1];
                    String res_id=splited[2];
                    String res_add=receivePacket.getAddress().getHostAddress();
                    System.out.println("\n> recieved respond: address= "+res_add+" , port= "+res_port+" , id= "+res_id);
                    try {

                        respond.add("address= "+res_add+" , port= "+res_port+" , id= "+res_id);
                        if (respond!=null){
                            System.out.println("reg: got repo");
                        }
                        System.out.println("out from find");
                    } catch (Exception e) {
                        System.err.println("Client exception: " + e.toString());
                        e.printStackTrace();
                    }
                }


//			dSocket.close();  //Close the port!
            }

        }
        catch (SocketTimeoutException e) {
            System.out.println(new RepException(e).getErrormsg());
        }
        catch (IOException ex) {
            System.out.println(new RepException(ex).getErrormsg());

        }finally {
            if(respond.size()>0)
                System.out.println("find it successfuly");
            else {
                System.out.println("wag wag wag");
            }
            String[] s=new String[respond.size()];
            for (int i = 0; i < respond.size(); i++) {
                System.out.println("--- "+respond.get(i));
                s[i] = respond.get(i);
            }
            return s;
        }
    }

    @Override
    public void registor(String id, URI uri) {
    }

    @Override
    public void unregistor(String id) {

    }


    public static String broadcastAndAskFromOtherReposetories (String repid) throws IOException, SocketTimeoutException {
        DatagramSocket dSocket;
        String respond ="error";
        int res_counter=0;
        try {
            //Open a random port to send the package
            dSocket = new DatagramSocket();
            dSocket.setSoTimeout(1000);
            dSocket.setBroadcast(true);
            byte[] sendData = ("PEER_REQUEST "+repid).getBytes();
            // Broadcast the message over all the network interfaces
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                if (networkInterface.isLoopback() || !networkInterface.isUp()) { continue; } // Omit loopbacks
                for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                    InetAddress broadcast = interfaceAddress.getBroadcast();
                    if (broadcast == null) { continue; } //Don't send if no broadcast IP.
                    try {
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcast, 6231);
                        dSocket.send(sendPacket); // Send the broadcast package!
                    }
                    catch (Exception e) {  //connection exception, unknown ones
                        System.out.println(new RepException(e).getErrormsg());
                    }
                    System.out.println("\n> Request sent to IP: "
                            + broadcast.getHostAddress() + "; Interface: " + networkInterface.getDisplayName());
                }
            }

            System.out.println("\n> Done looping through all interfaces. Now waiting for a reply!");





            byte[] recvBuf = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);

            while(true) {

                dSocket.receive(receivePacket); //Wait for a response
                //We have a response
                System.out.println("\n> Received packet from " + receivePacket.getAddress().getHostAddress() + " : " + receivePacket.getPort());
                String msg = new String(receivePacket.getData()).trim();
                String[] splited = msg.split(" ");
                if (splited[0].equals("PEER_RESPONSE")) {
                    if(res_counter>0){
                        throw new SocketTimeoutException();
                    }
                    res_counter+=1;
                    int res_port=Integer.valueOf(splited[1]);
                    String res_add=receivePacket.getAddress().getHostAddress();
                    System.out.println("\n> recieved respond: address= "+res_add+" , port= "+res_port);
                }


//			dSocket.close();  //Close the port!
            }
        }
        catch (SocketTimeoutException e) {
            if(res_counter>1){
                respond="error";
            }
            System.out.println(new RepException(e).getErrormsg());
        }
        catch (IOException ex) {
            System.out.println(new RepException(ex).getErrormsg());
        }finally {
            return respond;
        }
    }

}



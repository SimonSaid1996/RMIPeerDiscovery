package Client;
import Core.Connector;
import Core.IDistributedRepository;
import Core.Icallback;
import Core.RepException;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Scanner;


public class ClientApp extends UnicastRemoteObject implements Icallback, Serializable {
    Connector c;

    String portNum;
    public ClientApp() throws RemoteException {

        c = new Connector("r1","127.0.0.1",1111,this);
        while (true){
            Scanner myObj = new Scanner(System.in);

            String command = myObj.nextLine();
            if(command.equals("3")){
                break;
            }
            try {
                System.out.println(c.command(command));
            }catch (Exception e) {
                System.err.println("Client exception: " + e.toString());
                e.printStackTrace();
            }

        }
//        IDistributedRepository r=c.repogetRepo();
    }
    @Override
    public void EnumKeys(ArrayList<String> keys)throws RemoteException {    //not visible on the client

        for(int i=0;i<keys.size();i++){
            System.out.println(i+" : "+keys.get(i));
        }
    }

    @Override
    public void EnumValues( ArrayList<Integer> values)throws RemoteException {
        for(int i=0;i<values.size();i++){
            System.out.println(i+" : "+values.get(i));
        }
    }

    public static void main(String[] args) {
        try {
            new ClientApp();
        }catch (Exception e){

        }
    }
}

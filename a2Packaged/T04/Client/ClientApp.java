package Client;
import Core.Connector;
import Core.IDistributedRepository;
import Core.RepException;

import java.io.IOException;
import java.util.Scanner;


public class ClientApp {
    Connector c;

    String portNum;
    public ClientApp()  {

        c = new Connector("r1","127.0.0.1",1111);
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

    public static void main(String[] args) {
        new ClientApp();
    }
}

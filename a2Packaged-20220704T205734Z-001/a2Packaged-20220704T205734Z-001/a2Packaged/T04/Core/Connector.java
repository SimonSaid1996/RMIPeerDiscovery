package Core;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

public class Connector {
    IDistributedRepository repo=null;
    String id;
    String host;
    Integer port;
    public Connector(String id,String host,Integer port,Icallback c) {
        this.id=id;
        this.host=host;
        this.port=port;
        try {
            Registry registry = LocateRegistry.getRegistry(host, port);
            repo = (IDistributedRepository) registry.lookup(id);
            repo.setMycon(c);
            System.out.println("successfuly connected to object! id: "+id);
        } catch (Exception e) {
            System.out.println(new RepException(e).getErrormsg());

        }

    }
    public Connector(String id,String host,Integer port) {
        this.id=id;
        this.host=host;
        this.port=port;
        try {
            Registry registry = LocateRegistry.getRegistry(host, port);
            repo = (IDistributedRepository) registry.lookup(id);
            System.out.println("successfuly connected to object! id: "+id);
        } catch (Exception e) {
            System.out.println(new RepException(e).getErrormsg());

        }

    }


    public IRepository getRepo() {
        return repo;
    }



    public String command(String s)throws Exception {
        try {
            s=repo.connection(s);
        } catch (Exception e) {
            System.out.println(new RepException(e).getErrormsg());

        }
        return s;
//        return "done";
    }

    //not sure what should be in the connector class
}
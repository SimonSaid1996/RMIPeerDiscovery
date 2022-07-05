package ServiceDeamon;
import Core.RepException;
import Distribution.Registr;
import Core.IDistributedRepository;
import Repo.Reposetory;


import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RepositoryService {

    public static int port;
    public static String repid;
//    public static String host;

    private RepositoryService(){}

    public static void main(String[] args) {
        port = Integer.valueOf(args[1]);
        repid=args[0];
        Registr reg=new Registr(port,repid);
        try {
            Reposetory.initReposetory(reg,repid);
            IDistributedRepository reposetory = Reposetory.getInstance();
            IDistributedRepository stub = (IDistributedRepository) UnicastRemoteObject.exportObject(reposetory, 0);

            Registry registry = LocateRegistry.createRegistry(port);
            registry.rebind(repid, stub);
            System.out.println("got connected");

        } catch (Exception e) {
            System.out.println(new RepException(e).getErrormsg());

        }
    }

}

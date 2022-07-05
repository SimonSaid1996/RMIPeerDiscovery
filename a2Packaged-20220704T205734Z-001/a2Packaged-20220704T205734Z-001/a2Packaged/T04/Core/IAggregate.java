package Core;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface IAggregate extends Remote {  //extends Remote

    Integer sum(String key) throws RemoteException;

}
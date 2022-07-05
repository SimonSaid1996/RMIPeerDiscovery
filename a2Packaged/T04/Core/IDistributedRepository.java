package Core;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IDistributedRepository extends IRepository {
    IAggregate aggregate(String[] repids) throws RemoteException, RepException;
    String connection(String s)throws RemoteException;

}
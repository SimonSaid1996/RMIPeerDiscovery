package Core;

import Distribution.Registr;
import Repo.Reposetory;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Map;
/**
 * An interface for the remote object: Dictionary.java.
 * @author Pouria Roostaei
 */
public interface IRepository extends IAggregate {
    String connection(String s)throws RemoteException;
    public boolean addValue(String key,Integer value)throws RemoteException;

    public boolean set(String key, ArrayList<Integer> values)throws RemoteException;

    public boolean delete(String key,Integer value)throws RemoteException;
    ArrayList<String> list_keys()throws RemoteException;

    public boolean delete(String key)throws RemoteException;

    public String dsum(String[] repids,String key) throws RemoteException;
    public Integer getValue(String key)throws RemoteException;

    public ArrayList<Integer> getValues(String key)throws RemoteException;

    public void reset()throws RemoteException;
    void setMycon(Icallback c)throws RemoteException;

    public void enumKeys(Icallback c)throws RemoteException;

    public void enumValues(Icallback c,String key)throws RemoteException;
}
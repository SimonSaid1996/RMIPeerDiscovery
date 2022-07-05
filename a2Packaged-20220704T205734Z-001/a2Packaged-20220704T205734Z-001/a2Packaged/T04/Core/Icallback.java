package Core;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface Icallback extends Remote {
    void EnumKeys(ArrayList<String> keys) throws RemoteException;   //1 function to print the callbacks, get the strings and print them
    void EnumValues( ArrayList<Integer> values) throws RemoteException;
}

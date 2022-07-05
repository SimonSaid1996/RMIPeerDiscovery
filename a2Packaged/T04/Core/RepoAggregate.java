package Core;

import java.rmi.RemoteException;

public class RepoAggregate implements IAggregate {
    IRepository[] repos=null;
    public RepoAggregate(IRepository[] repos)throws RemoteException {
        this.repos=repos;
    }


    public Integer sum(String key) throws RemoteException {
        int dsum=0;
        for (IRepository rep : this.repos ){
            dsum+= rep.sum(key);
            System.out.println("dsum: "+dsum);
        }
        return dsum;
    }
}

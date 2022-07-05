package Core;

import java.rmi.Remote;
import java.util.Map;
import java.net.URI;
public interface IRegistry extends IDirectory {
    void registor (String id, URI uri) ;

    void unregistor (String id) ;

}
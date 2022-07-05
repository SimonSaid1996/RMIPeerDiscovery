package Repo;

import Core.*;
import Distribution.Registr;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

public class Reposetory implements IDistributedRepository {
    private HashMap<String, ArrayList<Integer>> dict = new HashMap<>();
    private static String id;
    private static Registr reg;

    Icallback myCon;

    private Reposetory(){}
    private static Reposetory singleton = new Reposetory( );
    public static Reposetory getInstance( ) {
        return singleton;
    }
    public static void initReposetory(Registr reg, String id)  {
        singleton.reg = reg;
        singleton.id = id;
    }
    public void setMycon(Icallback c){
        myCon = c;
    }


    public String connection(String s) throws RemoteException {


        System.out.println("Recieved msg: " + s);
        String[] command = s.split(" ");
        if (command.length > 3) {//inter if we have a dsum command
            if(command[0].equals("dsum")){
                String[] rids = new String[command.length - 3];
                for (int i = 3; i < command.length ; i++) {
                    if (command[i].equals(id)) {
//                    return("you dont need to put repid of this rep as well");
                        i++;
                    }

                    rids[i-3] = command[i];
                }
                try {
                    return dsum(rids,command[1]);
                } catch (Exception e) {
                    return new RepException(e).getErrormsg();
                }
            }
            else{
                 return (new RepException(new IllegalArgumentException()).getErrormsg());
            }
        } else {
            String[] keyParts = {""};
            if (command.length > 1) {
                keyParts = command[1].split("\\.");
            }
            if (keyParts.length > 1) {//multy part key
                command[1] = keyParts[1];
            }
            if ((keyParts.length > 1) && !(keyParts[0].equals(id))) {//comand is for other repo
                IRepository otherrepo = this.reg.find(keyParts[0]);

                String res = otherrepo.connection(String.join(" ", command));

                if (res == "error") {
                    res = "SERVER: ERR Non-existence or ambiguous repository " + keyParts[0];
                }
                return (res);
            } else {//command is for this repo
                try{
                    switch (command[0].toUpperCase()) {
                    case "OK":
                        for(String d:reg.list()){
                            System.out.println(d);
                        }
                        return (s);
                    case "SET":
                        ArrayList<Integer> values = new ArrayList<>(1);
                        if( command.length == 3 ){
                            for (int i = 2; i < command.length; i++) {
                                values.add(Integer.valueOf(command[i]));
                            }
                            this.set(command[1], values);
                            return ("OK");
                        }
                        else{
                            return(new RepException(new IllegalArgumentException()).getErrormsg());
                        }
                    case "DELETE":
                        Integer v = this.getValue(command[1]);
                        if (v != null) {  //only delete values that exist in the map
                            this.delete(command[1]);
                            return ("OK");
                        }
                        return(new RepException(new IllegalArgumentException()).getErrormsg());
                    case "ADD":    //kind of fixed
                        if(command.length==3){
                            boolean vExist = this.addValue(command[1], Integer.valueOf(command[2]));
                            if (vExist)
                                return ("OK");
                            else
                                return(new RepException(new NullPointerException()).getErrormsg());
                        }
                        else{
                            return(new RepException(new IllegalArgumentException()).getErrormsg());
                        }
                    case "GET":
                        if(command.length==2) {
                            ArrayList<Integer> value = this.getValues(command[1]);
                            if (value != null)
                                return ("OK " + value);
                            else
                                return(new RepException(new NullPointerException()).getErrormsg());
                        }
                        else{
                            return(new RepException(new IllegalArgumentException()).getErrormsg());
                        }

                    case "LISTKEY":   //might need to change the case name later, ask the prof
                        if(command.length==1){
                            return ("OK, KEYS ARE" + this.list_keys().toString());
                        }
                        else{
                            return(new RepException(new IllegalArgumentException()).getErrormsg());
                        }
                    case "RESET":
                        if(command.length==1){
                            this.reset();
                            return ("OK, ALL DATA RESET");
                        }
                        else{
                            return(new RepException(new IllegalArgumentException()).getErrormsg());
                        }
                    case "GETANY":
                        if(command.length==2){
                            return ("OK " + this.getValue(command[1]));
                        }
                        else{
                            return(new RepException(new IllegalArgumentException()).getErrormsg());
                        }
                    case "SUM":
                        if(command.length==2){
                            int sumV = 0;
                            sumV = this.sum(command[1]);  //update the sum value
                            return ("OK " + sumV);
                        }
                        else{
                            return(new RepException(new IllegalArgumentException()).getErrormsg());
                        }

                    case "ENUMKEYS":
                        if(command.length==1){
                            this.enumKeys(myCon);
                            return ("Ok");
                        }
                        else{
                            return(new RepException(new IllegalArgumentException()).getErrormsg());
                        }
                    case "ENUMVALUES":
                        if(command.length==2){
                            this.enumValues(myCon,command[1]);
                            return ("Ok");
                        }
                        else{
                            return(new RepException(new IllegalArgumentException()).getErrormsg());
                        }

                    case "BYE":
                        return ("CIAO Arrivederci!");
                    case "QUIT":
                        return ("BYE, it was nice seeing you.");

                    default:
                        return (new RepException(new IllegalArgumentException()).getErrormsg());
                    }
                }catch (Exception e){
                    return new RepException(e).getErrormsg();
                }
            }
        }
//        return "done";
    }
    public String dsum(String[] repids,String key) throws RemoteException{
        Integer sumation=sum(key);
        sumation+=aggregate(repids).sum(key);
        return sumation.toString();
    }
    public boolean addValue(String key, Integer value) throws RemoteException {
        if (dict.containsKey(key)) {
            dict.get(key).add(value);
            return true;
        }
        return false;
    }
    public boolean set(String key, ArrayList<Integer> values) throws RemoteException {
        if (dict.containsKey(key)) {
            dict.remove(key);
        }
        dict.put(key, values);
        return true;
    }

    public boolean delete(String key, Integer value) throws RemoteException {
        if (!dict.containsKey(key)) {
            return false;
        }
        if (dict.get(key).remove(value)) {
            return true;
        }
        return false;
    }
    public boolean delete(String key) throws RemoteException {
        if (!dict.containsKey(key)) {
            return false;
        }
        dict.remove(key);
        return true;
    }
    public ArrayList<String> list_keys() throws RemoteException {
        return new ArrayList<String>(dict.keySet());
    }
    public Integer getValue(String key) throws RemoteException {
        if (!dict.containsKey(key)) {
            return null;
        }
        return dict.get(key).get(0);
    }
    public ArrayList<Integer> getValues(String key) throws RemoteException {
        if (!dict.containsKey(key)) {
            return null;
        }
        return dict.get(key);
    }
    public Integer sum(String key) throws RemoteException {
        ArrayList<Integer> values = dict.get(key);
        Integer sum = 0;
        for (int i = 0; i < values.size(); i += 1) {
            sum += values.get(i);
        }
        return sum;
    }
    public void reset() throws RemoteException {
        dict.clear();
    }
    @Override
    public void enumKeys(Icallback c) throws RemoteException {
        System.out.println();

        ArrayList<String> keyList = new ArrayList<>();
        for (String key : dict.keySet()) {
            keyList.add(key);
        }
        c.EnumKeys(keyList);
    }
    @Override
    public void enumValues(Icallback c,String key) throws RemoteException {
        c.EnumValues(dict.get(key));
    }

    public IAggregate aggregate(String[] repids) throws RemoteException {
        IRepository[] repos =new IRepository[repids.length];
        System.out.println(repids);
        for(int i=0 ; i<repids.length;i++){
            if(repids[i].equals(this.id)) {
                repos[i] = this;
            }else{
                repos[i]=reg.find(repids[i]);
            }
        }
        return new RepoAggregate(repos);
    }
}

package Core;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.rmi.RemoteException;

/**
 * You need to write Exception Handler for RMI and Spring Boot Exceptions!
 * Check the error handling in RMI and Spring Boot to complete this class.
 */
public class RepException extends Exception {   //IOException

    String errormsg="error occured";

    public RepException(Throwable cause) {
        super();
        if (cause instanceof RemoteException) {
            errormsg="Something wrong with RMI. Please fix!";
        }
        else if (cause instanceof NumberFormatException) {
            errormsg="should input an integer, not a String. Please fix!";
        }
        else if (cause instanceof IOException) {
            errormsg="IOException: Please fix!";
        }
        else if(cause instanceof ArrayIndexOutOfBoundsException){
            errormsg="ArrayIndex out of bound: Please check the number of argument numbers and fix!";
        }
        else if(cause instanceof IllegalArgumentException){
            errormsg="illegal argument: the input length is wrong or argument doesn't exist,please fix ";
        }
        else if(cause instanceof NullPointerException){
            errormsg="null point exception: can't find object in the Dictionary, check your parameters, please fix";
        }
        else if(cause instanceof SocketTimeoutException){
            errormsg ="SERVER: ERR Non-existence or ambiguous repository";
        }
        else {
            //errormsg = cause.toString();
            errormsg="Some other exceptions. Please fix!";
        }
//        System.out.println(errormsg);
    }

    public String getErrormsg() {
        return "RepException Error: "+errormsg;
    }
}
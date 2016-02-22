package com.example.lukie.criptoim;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by lukie on 17/02/2016.
 */
public class SocketHandler {
    private static Socket socket;
    private static ObjectInputStream input;
    private static ObjectOutputStream output;

    public static synchronized Socket getSocket(){
        return socket;
    }

    public static synchronized ObjectInputStream getInput(){
        return input;
    }

    public static synchronized ObjectOutputStream getOutput(){
        return output;
    }

    public static synchronized void setSocket(Socket socket){
        SocketHandler.socket = socket;
    }
    public static synchronized void setInput(ObjectInputStream input){
        SocketHandler.input = input;
    }
    public static synchronized void setOutput(ObjectOutputStream output){
        SocketHandler.output = output;
    }
}
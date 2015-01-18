package com.gmail.josephui.simplentpservice.server;

import com.gmail.josephui.simplentpservice.TestMod;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This singleton class represent the Producer as specified in the 
 * specification.
 * 
 * @author Joseph Hui
 * @version 2015.1.17
 */
public final class Producer extends Thread{
    /**
     * The default port to use for the ServerSocket.
     */
    public static final int DEFAULT_PORT = 29999;
    
    /**
     * Every DELAY_BETWEEN_TIME_MESSAGES milliseconds, the Producer will send a 
     * TIME message to all registered Consumers.
     */
    public static final long DELAY_BETWEEN_TIME_MESSAGES = 1000;
    
    /**
     * If no KeepAlive message is received from a given Consumer within 
     * KEEP_ALIVE_TIME milliseconds, the Producer will stop sending messages to 
     * that specific Consumer.
     */
    public static final long KEEP_ALIVE_TIME = 10000;
    
    
    /**
     * Singleton class instance. This instance variable will hold the only 
     * instance of the Producer class after the getInstance() method has been 
     * called.
     */
    private static Producer instance;
    
    /**
     * This method returns the singleton instance of the Producer class.
     * 
     * @return the singleton instance of the Producer class
     */
    public static Producer getInstance() throws IOException{
        if(instance == null){
            instance = new Producer();
        }
        return instance;
    }
    
/*------------------------------------------------------------------------------
START NON-STATIC
------------------------------------------------------------------------------*/
    
    /**
     * This thread listens to the server port and wait for connecting clients 
     * (Consumers).
     */
    private final Thread acceptingClientsThread;
    
    /**
     * Every DELAY_BETWEEN_TIME_MESSAGES milliseconds, this thread sends TIME 
     * messages to clients (Consumers) that has both (1) REGISTER itself with 
     * the server, and (2) has send a KEEP_ALIVE message to the server within
     * the past KEEP_ALIVE_TIME milliseconds.
     */
    private final Thread sendingClientsTimeMessagesThread;
    
    /**
     * The ServerSocket that will listen for incoming client connection.
     */
    private final ServerSocket serverSocket;
    
    //Not used, but could potentially be of use if increased functionality are 
    //implemented
    //private final Set<ClientHandler> clients;
    
    private Producer() throws IOException{
        serverSocket = new ServerSocket(DEFAULT_PORT);
        //clients = Collections.synchronizedSet(new HashSet<ClientHandler>());
        
        //The thread is made on the fly because it's a single purpose thread
        acceptingClientsThread = new Thread(){
            @Override
            public void run(){
                while(true){
                    try{
                        TestMod.printlnIfTest("acceptingClientsThread", "Listening for Consumer connection");
                        Socket client = serverSocket.accept();
                        TestMod.printlnIfTest("acceptingClientsThread", "Accepted a Consumer connection");
                        
                        //The ClientHandler Thread listens to inputs that the 
                        //client sends to the server and processes them
                        ClientHandler handler = new ClientHandler(client);
                        //clients.add(handler);
                        handler.start();
                    }catch(IOException ioe){
                        System.out.println(ioe);
                        return;
                    }
                }
            }
        };
        
        //The thread is made on the fly because it's a single purpose thread
        sendingClientsTimeMessagesThread = new Thread(){
            long nextSendTime;
            @Override
            public void run(){
                nextSendTime = System.currentTimeMillis();
                while(true){
                    while(System.currentTimeMillis() < nextSendTime){
                        try{
                            Thread.sleep(10);
                        }catch(InterruptedException ie){
                            //Impossible because no other thread is calling 
                            //interrupt()
                            throw new IllegalStateException("Impossible event.");
                        }
                    }
                    //This method of counting time is more accurate in the long 
                    //run than simply making the Thread sleep for 
                    //DELAY_BETWEEN_TIME_MESSAGES milliseconds.
                    nextSendTime += DELAY_BETWEEN_TIME_MESSAGES;
                    
                    //Checks for timed out clients and remove them
                    ClientHandler.removeClientsOlderThan(System.currentTimeMillis() - KEEP_ALIVE_TIME);
                    
                    TestMod.printlnIfTest("sendingClientsTimeMessagesThread", "Sending out Time messages");
                    //Gives the active clients the time message
                    ClientHandler.sendTimeMessageToListeningClients();
                }
            }
        };
    }
    
    @Override
    public void run(){
        acceptingClientsThread.start();
        sendingClientsTimeMessagesThread.start();
    }
}

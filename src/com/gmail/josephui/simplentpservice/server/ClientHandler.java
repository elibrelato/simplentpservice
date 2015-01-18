package com.gmail.josephui.simplentpservice.server;

import com.gmail.josephui.simplentpservice.TestMod;
import com.gmail.josephui.simplentpservice.client.ConsumerOpcode;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Instances of this class listens to clients that has been accepted by the 
 * Producer
 * 
 * @author Joseph Hui
 * @version 2015.1.17
 */
public final class ClientHandler extends Thread{
    /**
     * Contains the list of currently 'listening' Consumer connections, 
     * 'listening' is defined to be a connection that has received a KeepAlive 
     * message within the past alloted time. A HashSet is used for this 
     * implementation due to fastest average performance (O(1)). However, a 
     * TreeSet may be used instead if memory space is of concern.
     */
    private static final Set<ClientHandler> listeningClients;
    
    static{
        //Wrap it around with synchronizedSet for deterministic behavior
        listeningClients = Collections.synchronizedSet(new HashSet<ClientHandler>());
    }
    
    static int counter = 1;
    /**
     * Removes all Consumers that has last send their KeepAlive message before 
     * timeMinimum
     * 
     * @param timeMinimum the time in milliseconds since the epoch
     */
    public static void removeClientsOlderThan(long timeMinimum){
        synchronized(listeningClients){
            LinkedList<ClientHandler> toBeRemovedClients = new LinkedList();
            
            for(ClientHandler client : listeningClients){
                if(client.lastKeepAliveReceiveTime.get() < timeMinimum){
                    //Remove has to be done outside iterator
                    toBeRemovedClients.add(client);
                    
                    //Testing code
                    //System.out.println("[Producer] " + counter++);
                }
            }
            
            for(ClientHandler client : toBeRemovedClients){
                try{
                    client.in.close();
                    client.out.close();
                    client.socket.close();
                }catch(IOException ioe){
                    //Problem closing stream..
                }
                listeningClients.remove(client);
            }
        }
    }
    
    /**
     * Sends a time sensitive packet explicitly generated for each individual 
     * client.
     */
    public static void sendTimeMessageToListeningClients(){
        synchronized(listeningClients){
            TestMod.printlnIfTest("static ClientHandler", "Total listening clients: " + listeningClients.size());
            for(ClientHandler client : listeningClients){
                try{
                    byte[] timePacket = ProducerPacketFactory.getTimeMessagePacket(System.currentTimeMillis());
                    client.out.write(timePacket);
                    client.out.flush();
                }catch(IOException ioe){
                    client.interrupt();
                }
            }
        }
    }
    
    //The following method would be used if more general (not as time 
    //sensitive) packets were to be send
    /*
    public static void sendMessageToListeningClients(byte[] packet){
        synchronized(listeningClients){
            for(ClientHandler client : listeningClients){
                try{
                    client.out.write(packet);
                    client.out.flush();
                }catch(IOException ioe){
                    //
                }
            }
        }
    }
    */
    
/*------------------------------------------------------------------------------
START NON-STATIC
------------------------------------------------------------------------------*/
    
    /**
     * This ClientHandler is connected to the client through this Socket
     */
    private final Socket socket;
    
    /**
     * The InputStream that accepts data from the client
     */
    private final InputStream in;
    
    /**
     * The OutputStream that will take data to be send to the client
     */
    private final OutputStream out;
    
    /**
     * This keeps track of the last time a KEEP_ALIVE message has been 
     * received, the implementation uses a AtomicLong because it could be 
     * potentially accessed by multiple Threads
     */
    private final AtomicLong lastKeepAliveReceiveTime;
    
    public ClientHandler(Socket _socket) throws IOException{
        socket = _socket;
        in = _socket.getInputStream();
        out = _socket.getOutputStream();
        lastKeepAliveReceiveTime = new AtomicLong();
    }
    
    @Override
    public void run(){
        try{
            while(!interrupted()){
                TestMod.printlnIfTest("ClientHandler", "Waiting on incoming Opcode");
                //Identify the operation of the request
                ConsumerOpcode operation = ConsumerOpcode.getByOpcode((byte)in.read());
                //If operation is null it's not a defined operation
                assert(operation != null);
                TestMod.printlnIfTest("ClientHandler", "Opcode: " + operation);
                
                //The following code is used if more Opcodes that include 
                //additional information are implemented
                /*
                int length = in.read();
                
                //BufferedInputStream is not used here because the read() 
                //does not guarentee to read all the bytes before returning.
                byte[] packet = new byte[length];
                for(int i = 0; i < packet.length; i++){
                    packet[i] = (byte)in.read();
                }
                
                //Set up buffer to decipher the packet
                ByteBuffer buffer = ByteBuffer.wrap(packet);
                */
                
                //Process the request
                //If more Opcodes are used, dedicated classes implementing an 
                //interface could be used to process each instruction for
                //clarity
                switch(operation){
                    case REGISTER:
                        listeningClients.add(this);
                        //No break here so that the initial receive time is set
                    case KEEP_ALIVE:
                        lastKeepAliveReceiveTime.set(System.currentTimeMillis());
                }
            }
        }catch(IOException ioe){
            //Connection severed, either by client, or closed from the static 
            //method removeClientsOlderThan(long)
        }
    }
}

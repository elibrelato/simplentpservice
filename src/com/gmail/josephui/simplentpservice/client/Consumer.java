package com.gmail.josephui.simplentpservice.client;

import com.gmail.josephui.simplentpservice.TestMod;
import static com.gmail.josephui.simplentpservice.server.Producer.DEFAULT_PORT;
import com.gmail.josephui.simplentpservice.server.ProducerOpcode;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Random;

/**
 * This class represent a Consumer as specified in the specification.
 * 
 * @author Joseph Hui
 * @version 2015.1.17
 */
public class Consumer extends Thread{
    /**
     * This is the host address to which this Consumer will attempt to connect
     */
    protected static final String SERVER_ADDRESS = "localhost";
    
    /**
     * This specific the delay between KEEP_ALIVE messages, in milliseconds
     */
    protected static final long DELAY_BETWEEN_KEEP_ALIVE_MESSAGES = 5000;
    
    /**
     * These two constants specifies the minimum and maximum number of 
     * KEEP_ALIVE messages a Consumer may send out, respectively.
     */
    protected static final int MINIMUM_KEEP_ALIVE_MESSAGES = 0;
    protected static final int MAXIMUM_KEEP_ALIVE_MESSAGES = 12;
    
    /**
     * This is the Random object used when generating a number between 
     * MINIMUM_KEEP_ALIVE_MESSAGES and MAXIMUM_KEEP_ALIVE_MESSAGES
     */
    protected static final Random rand;
    
    static{
        rand = new Random();
    }
    
    /**
     * Generates a random number between MINIMUM_KEEP_ALIVE_MESSAGES and 
     * MAXIMUM_KEEP_ALIVE_MESSAGES, inclusive.
     */
    protected static int generateTotalKeepAliveMessages(){
        return rand.nextInt(MAXIMUM_KEEP_ALIVE_MESSAGES - MINIMUM_KEEP_ALIVE_MESSAGES + 1) + MINIMUM_KEEP_ALIVE_MESSAGES;
    }
    
/*------------------------------------------------------------------------------
START NON-STATIC
------------------------------------------------------------------------------*/
    
    /**
     * This Consumer is connected to the server through this Socket
     */
    protected final Socket socketToServer;
    
    /**
     * The InputStream that accepts data from the server
     */
    protected final InputStream in;
    
    /**
     * The OutputStream that will take data to be send to the server
     */
    protected final OutputStream out;
    
    /**
     * The name of this Consumer, this is used when printing out messages to 
     * the console
     */
    protected final String consumerName;
    
    /**
     * This Thread periodically sends KEEP_ALIVE messages to the server that 
     * has both (1) REGISTER itself with the server, and (2) has send a 
     * KEEP_ALIVE message to the server within the past KEEP_ALIVE_TIME 
     * milliseconds.
     */
    protected final Thread sendingServerMessagesThread;
    
    /**
     * This Thread listen to inputs from the server and processes them.
     */
    protected final Thread receivingServerMessagesThread;
    
    public Consumer(String _consumerName) throws IOException{
        socketToServer = new Socket(SERVER_ADDRESS, DEFAULT_PORT);
        in = socketToServer.getInputStream();
        out = socketToServer.getOutputStream();
        
        consumerName = _consumerName;
        
        //The thread is made on the fly because it's a single purpose thread
        sendingServerMessagesThread = new Thread(){
            long nextSendTime;
            @Override
            public void run(){
                //Sends the REGISTER message to the server
                try{
                    sendMessageToServer(ConsumerPacketFactory.getRegisterMessagePacket());
                }catch(IOException ioe){
                    System.err.println(ioe);
                    return;
                }
                nextSendTime = System.currentTimeMillis() + DELAY_BETWEEN_KEEP_ALIVE_MESSAGES;
                //This variable keeps track of the number of remaining 
                //KEEP_ALIVE message this thread will send out
                int remainingKeepAliveMessages = generateTotalKeepAliveMessages();
                while(--remainingKeepAliveMessages >= 0){
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
                    //DELAY_BETWEEN_KEEP_ALIVE_MESSAGES milliseconds.
                    try{
                        byte[] keepAlivePacket = ConsumerPacketFactory.getKeepAliveMessagePacket();
                        nextSendTime = System.currentTimeMillis() + DELAY_BETWEEN_KEEP_ALIVE_MESSAGES;
                        
                        TestMod.printlnIfTest("sendingServerMessagesThread", "Sending KeepAlive Message. Remaining messages: " + remainingKeepAliveMessages);
                        sendMessageToServer(keepAlivePacket);
                    }catch(IOException ioe){
                        System.err.println(ioe);
                        return;
                    }
                }
            }
        };
        
        //The thread is made on the fly because it's a single purpose thread
        receivingServerMessagesThread = new Thread(){
            @Override
            public void run(){
                try{
                    int b;
                    while((b = in.read()) != -1){
                        TestMod.printlnIfTest("receivingServerMessagesThread", "Reading request");
                        //Identify the operation of the request
                        ProducerOpcode operation = ProducerOpcode.getByOpcode((byte)b);
                        //If operation is null it's not a defined operation
                        assert(operation != null);
                        
                        //The length of the rest of the packet (in bytes)
                        int length = in.read();

                        //BufferedInputStream is not used here because the read() 
                        //does not guarentee to read all the bytes before returning.
                        byte[] packet = new byte[length];
                        for(int i = 0; i < packet.length; i++){
                            packet[i] = (byte)in.read();
                        }

                        //Set up buffer to decipher the packet
                        ByteBuffer buffer = ByteBuffer.wrap(packet);
                        
                        switch(operation){
                            case TIME:
                                long time = buffer.getLong();
                                System.out.println("[" + consumerName + "]" + " CurrentTime: " + time);
                        }
                    }
                }catch(IOException ioe){
                    System.err.println(ioe);
                }
            }
        };
    }
    
    @Override
    public void run(){
        sendingServerMessagesThread.start();
        receivingServerMessagesThread.start();
    }
    
    public void sendMessageToServer(byte[] packet) throws IOException{
        out.write(packet);
        out.flush();
    }
}

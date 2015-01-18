package com.gmail.josephui.simplentpservice;

import com.gmail.josephui.simplentpservice.client.Consumer;
import com.gmail.josephui.simplentpservice.server.Producer;
import java.io.IOException;

/**
 * This class provides the entry point for the application
 * 
 * @author Joseph Hui
 * @version 2015.1.17
 */
public final class Main{
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args){
        //Load test mode argument, or use args if test mode is off
        args = TestMod.getArgs(args);
        
        //This checks if the user has supplied a command line argument
        if(args.length == 0){
            System.err.println("Please provide the number of Consumers to start");
            return;
        }
        
        //This checks if the users has supplied a valid number in the command 
        //line argument
        int numConsumers = 0;
        try{
            numConsumers = Integer.parseInt(args[0]);
            if(numConsumers <= 0){
                throw new NumberFormatException();
            }
        }catch(NumberFormatException nfe){
            System.err.println("Please enter a valid number of Consumers");
            return;
        }
        
        //Start the server (Producer)
        TestMod.printlnIfTest("Main", "Starting Producer");
        try{
            Producer.getInstance().start();
        }catch(IOException ioe){
            System.err.println("Unable to init server (Producer)");
            return;
        }
        
        //Generate the clients (Consumers) and start them
        for(int i = 1; i <= numConsumers; i++){
            TestMod.printlnIfTest("Main", "Starting Consumer " + i);
            try{
                new Consumer("Consumer " + i).start();
            }catch(IOException ioe){
                System.err.println("Error connecting to server (Consumer #" + i + ")");
            }
        }
    }
    
/*------------------------------------------------------------------------------
START NON-STATIC
------------------------------------------------------------------------------*/
    
    private Main(){}
}

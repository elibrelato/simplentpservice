package com.gmail.josephui.simplentpservice.client;

import static com.gmail.josephui.simplentpservice.client.ConsumerOpcode.*;

/**
 * This factory class provides the method to generate packets to be send to the 
 * Producer.
 * 
 * @author Joseph Hui
 * @version 2015.1.17
 */
public final class ConsumerPacketFactory{
    static{
        registerMessagePacket = new byte[]{
            REGISTER.opcode()
        };
        
        keepAliveMessagePacket = new byte[]{
            KEEP_ALIVE.opcode()
        };
    }
    
    /**
     * Construct a Register message packet to be send to Producer. The size is 
     * 1, and the information will be encoded as follows:
     * byte 0: The Opcode of the Register message
     */
    private static final byte[] registerMessagePacket;
    protected static byte[] getRegisterMessagePacket(){
        return registerMessagePacket;
    }
    
    /**
     * Construct a KeepAlive message packet to be send to Producer. The size 
     * is 1, and the information will be encoded as follows:
     * byte 0: The Opcode of the KeepAlive message
     */
    private static final byte[] keepAliveMessagePacket;
    protected static byte[] getKeepAliveMessagePacket(){
        return keepAliveMessagePacket;
    }
    
/*------------------------------------------------------------------------------
START NON-STATIC
------------------------------------------------------------------------------*/
    
    private ConsumerPacketFactory(){}
}

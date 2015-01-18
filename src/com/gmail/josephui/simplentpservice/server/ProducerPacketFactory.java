package com.gmail.josephui.simplentpservice.server;

import static com.gmail.josephui.simplentpservice.server.ProducerOpcode.*;
import java.nio.ByteBuffer;

/**
 * This factory class provides the method to generate packets to be send to a 
 * Consumer.
 * 
 * @author Joseph Hui
 * @version 2015.1.17
 */
public final class ProducerPacketFactory{
    protected static byte[] getTimeMessagePacket(long time){
        /**
         * Construct a ByteBuffer that will pack the information packet to be 
         * send to Consumer. The size is allocated to be 10, and the 
         * information will be encoded as follows:
         * byte 0: The Opcode of the Time message
         * byte 1: The size of the remaining number of bytes, 9
         * byte 2-9: The time specified encoded into a series of bytes
         */
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put(TIME.opcode());
        buffer.put((byte)8);
        buffer.putLong(time);
        return buffer.array();
    }
    
/*------------------------------------------------------------------------------
START NON-STATIC
------------------------------------------------------------------------------*/
    
    private ProducerPacketFactory(){}
}

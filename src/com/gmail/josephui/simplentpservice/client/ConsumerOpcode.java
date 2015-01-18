package com.gmail.josephui.simplentpservice.client;

/**
 * This class provides the enumerate constants for messages from the Consumer 
 * to the Producer
 * 
 * @author Joseph Hui
 * @version 2015.1.17
 */
public enum ConsumerOpcode{
    //These Opcodes represent the operations to send the REGISTER and 
    //KEEP_ALIVE messages, respectively
    REGISTER,
    KEEP_ALIVE;
    
    //Using byte representation, this allows for up to 256 opcodes
    public byte opcode(){
        return (byte)ordinal();
    }
    
/*------------------------------------------------------------------------------
START STATIC
    ------------------------------------------------------------------------------*/
    
    public static ConsumerOpcode getByOpcode(byte opcode){
        return (opcode < values().length) ? values()[opcode] : null;
    }
}

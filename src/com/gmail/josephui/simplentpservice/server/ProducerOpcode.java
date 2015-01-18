package com.gmail.josephui.simplentpservice.server;

/**
 * This class provides the enumerate constants for messages from the Producer 
 * to the Consumer
 * 
 * @author Joseph Hui
 * @version 2015.1.17
 */
public enum ProducerOpcode{
    //This Opcode represent the operations to send the TIME message
    TIME;
    
    //Using byte representation, this allows for up to 256 opcodes
    public byte opcode(){
        return (byte)ordinal();
    }
    
/*------------------------------------------------------------------------------
START STATIC
------------------------------------------------------------------------------*/
    
    public static ProducerOpcode getByOpcode(byte opcode){
        return (opcode < values().length) ? values()[opcode] : null;
    }
}

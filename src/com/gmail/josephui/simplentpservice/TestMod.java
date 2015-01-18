package com.gmail.josephui.simplentpservice;

/**
 * This class allows for simple toggle of Test mode in testing the software.
 * 
 * @author Joseph Hui
 * @version 2015.1.17
 */
public final class TestMod{
    /**
     * This flag indicates whether the program is to be run in test mode. In 
     * test mode, the program will use the value of TEST_MODE_NUM_CONSUMERS in 
     * place of the input from the command line argument.
     */
    public static final boolean TEST_MODE_ON = true;
    
    /**
     * This number specifies how many numbers of consumers are simulated during 
     * test mode.
     */
    public static final int TEST_MODE_NUM_CONSUMERS = 10;

    /**
     * This method will print out to the standard output stream test mode is 
     * on, if it is indeed on, then:
     * If TestMode is toggled, this will return the TestMode values, otherwise 
     * the given args will be returned.
     * 
     * @param args the actual command line argument
     * @return TestMode arguments if TestMode is on, args otherwise
     */
    public static String[] getArgs(String[] args){
        if(TEST_MODE_ON){
            printlnIfTest("TestMod", "Test mode is on.");
            return new String[]{
                "" + TEST_MODE_NUM_CONSUMERS
            };
        }
        return args;
    }
    
    /**
     * Prints the header, followed by the given text to standard output stream, 
     * and then prints out with the new line character
     * 
     * @param header the name of calling object
     * @param text the text to display
     */
    public static void printlnIfTest(String header, String text){
        if(TEST_MODE_ON){
            System.out.println("\t[" + header + " / TestMode] " + text);
        }
    }
    
/*------------------------------------------------------------------------------
START NON-STATIC
------------------------------------------------------------------------------*/
    
    private TestMod(){}
}

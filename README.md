OVERVIEW
--------
This Java application provides a simple implementation of a NTP service. The architecture is 
laid out as follows: 

(1) The class com.gmail.josephui.simplentpservice.Main, which provides the entry point for 
the application, 

(2) The class com.gmail.josephui.simplentpservice.TestMod, which provides the toggle and 
supporting methods in Testing the application,

(3) The four classes (and their respective inner classes) under the package 
com.gmail.josephui.simplentpservice.server, which includes the Producer class and other 
classes that supports it's functionality, and, 

(4) The three classes (and their respective inner classes) under the package 
com.gmail.josephui.simplentpservice.server, which includes the Consumer class and other 
classes that supports it's functionality.


DESIGN DECISIONS
----------------
- The Networking protocol used in the solution is java.net.Socket and java.net.ServerSocket 
as they provide a simple and working solution to the project requirement.

- Communication (messages) between the Producer (server) and Consumers (clients) are done 
via simple packets, which includes an initial Opcode specifying the instruction, followed by 
(if applicable) the length of the rest of packet, and the content of the packet. This design 
allows for simple integration of new types of messages between the server and clients.

- The container used for keeping track of Consumers who have send their REGISTER message and 
before they TIME_OUT is a HashSet wrappered in Collections.synchronizedSet(Set). HashSet is 
chosen for the superior average time on the operations insert and delete. Alternatively if 
memory space is of concern, a TreeSet could be used instead by trading slight performance 
decrease for more efficent memory usage. Regardless, the implementing AbstractSet is 
wrappered in Collections.synchronizedSet(Set) to prevent concurrency problems.

- The Producer and Consumers technically belongs to two (or more) distinct programs, the 
seperation of packages reflects this. 

- The Producer class is implemented using the singleton class. This is the logical solution 
because there is only one Producer, but many Consumers.

- The ProducerPacketFactory and ConsumerPacketFactory provides Factory methods in generating 
respective packets to be use in the communication between the server and clients. Factory 
methods allows for simplicity and clarity of code, as well as reusability.

- Test mode can be toggled by changing the boolean TEST_MODE_ON in 
com.gmail.josephui.simplentpservice.TestMod , test mode display extra messages to console 
regarding the status of the application.


POTENTIAL IMPROVEMENTS
----------------------
- For a larger Opcode base, the switch statements that handle the Opcodes can be replaced by 
having dedicated classes implementing an interface that contains the method "process(Opcode, 
byte[])"  for code clarity.


HOW TO RUN
----------
This application requires the Java Development Kit (JDK), please visitwww.java.com to download the latest version of JDK and JRE to ensure compability.

Assuming you have JDK correctly installed in your system, you may compile the source files in Windows by the following:
In command line prompt, navigate to the folder containing the build folder of this 
application, then enter the following:

javac -d ./build/classes src/com/gmail/josephui/simplentpservice/*.java src/com/gmail/josephui/simplentpservice/client/*.java src/com/gmail/josephui/simplentpservice/server/*.java

Assuming you have complied the Java source code via the above step, you may run this application in Windows by the following:
In command line prompt, navigate to the folder containing the build folder of this application, then enter the following:

java -cp ./build/classes/ com.gmail.josephui.simplentpservice.Main NUM_CONSUMERS

where NUM_CONSUMERS is the number of consumers this application will start with, as denoted in the specification.

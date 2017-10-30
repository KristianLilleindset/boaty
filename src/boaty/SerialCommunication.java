/*
 * This class handles the serial communication with the Arduino
 * it creates a connenction with the device on the connected Serial
 * port. Then creates a reader and a writer. 
 */
package boaty;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;
import java.util.Enumeration;
import java.util.TooManyListenersException;

/**
 *
 * @author kristianandrelilleindset
 */
public class SerialCommunication implements SerialPortEventListener
{
    // variable holding the chosen serialport
    public SerialPort serialPort;
    
    // list holding all of the desired available serialports
    private static final String PORT_NAMES[] = {"/dev/cu.usbserial-DN01DC4S"}; //MAC
    
    // variable holding the 
    private static final int TIME_OUT = 2000;
    
    // variable holding the desired rate of sending and receiving data 
    private static final int DATA_RATE = 9600;
    
    // variable holding the serialwriter instance
    private SerialWriter writer;
    
    // variable holding the serialreader instance
    private SerialReader reader;
    
    // variable holding the received data from the serialreader
    private byte receivedData;
    
    
    
    /**
     * 
     */
    public SerialCommunication()
    {

        
    }
    
    
    /**
     * 
     */
    private void connect() throws TooManyListenersException
    {
        CommPortIdentifier portId = null;
        
        Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();
        
        while(portEnum.hasMoreElements())
        {
            CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
            
            for(String portName : PORT_NAMES)
            {
                if(currPortId.getName().equals(portName))
                {
                    portId = currPortId;
                    
                    break;
                }
            }
        }
        
        // check if a port was found
        if(portId == null)
        {
            System.out.println("Could not find COM port...");    
            return;
        }
        
        try
        {
            // open serialPort
            serialPort = (SerialPort) portId.open(this.getClass().getName(), TIME_OUT);
            
            // set the serialport prameters 
            serialPort.setSerialPortParams(DATA_RATE,
                                           SerialPort.DATABITS_8,
                                           SerialPort.STOPBITS_1,
                                           SerialPort.PARITY_NONE);
            
            
            System.out.println("Connected to serialport: " + portId);
            
            // create and start writer thread
            writer = new SerialWriter(serialPort);
            
            // create and start reader thread
            reader =  new SerialReader(serialPort);        
        } 
        
        catch (PortInUseException | UnsupportedCommOperationException e) 
        {
            System.out.println(e.toString());
        }
    }
    
    /**
     * close connection to the port.
     */
    public synchronized void close()
    {
        if (serialPort != null)
        {
            serialPort.removeEventListener();
            
            serialPort.close();
        }
    }
    
    
    
    /**
     *  Send data by making a call to the serialwriter object.
     * 
     * @param sendData the data to be sent to the serialwriter
     */
    public void sendData(byte[] sendData)
    {
        this.writer.sendData(sendData);
    }
    
    
    /**
     * Get the data received from the serialport
     * @return the received data
     */
    
    public byte getReceivedData()
    {
        
        return receivedData;
    }
    
    
    /**
     * 
     */
    public void run()
    {
        try 
        {
            this.connect();
        } catch (TooManyListenersException ex) 
        {
            System.out.println(ex.toString());
        }
    }

    @Override
    public void serialEvent(SerialPortEvent spe) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}

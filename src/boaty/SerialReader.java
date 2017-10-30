/*
 * This class is responsible for reading the incoming data from the Arduino
 * When the input data has been read it is checked for the correct data. When 
 * the data has been accepted the listeners for the data is being notified. 
 */
package boaty;

import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.TooManyListenersException;

/**
 *
 * @author kristianandrelilleindset
 */
public class SerialReader implements SerialPortEventListener
{
    private final SerialPort serialPort;
 
    // variable holding the 
    private InputStream input;
    
    // byte array holding the data being received
    private byte[] inputData = null; 
    
    // byte array holding the data that has been received and not being correupted
    private byte[] acceptedData;
    
    // variable holding the expected amount of input bytes
    // startbyte, data, data, data, data, stopbyte
    private final int nrOfExpectedInputBytes = 6;
    
    // variable holding the number of the position of the first byte being received
    private final int firstByte = 0;
    
    // variable holding the number of the position of the last byte being received
    private final int lastByte = 5;
    
    // arrayList holding all of the listeners interested in the serial communication
    private final ArrayList<SerialInputListener> listeners;
    
        
    

    /**
     * 
     * @param serialPort, the serialport being used for serial communication 
     * with the Arduino
     * @throws TooManyListenersException 
     */
    public SerialReader(SerialPort serialPort) throws TooManyListenersException
    {
        // creating the arrayList of listeners 
        listeners = new ArrayList<>();
        
        
        this.serialPort = serialPort;  
        this.initialize();
    }

    
    public void addListener(SerialInputListener listener)
    {
        this.listeners.add(listener);
    }
    
    
    /**
     * Initalize.
     * Adds eventlistener to the inputm and creates an inputstream
     * @throws TooManyListenersException 
     */
    private void initialize() throws TooManyListenersException
    {
        // add eventlisteners   
        serialPort.addEventListener(this);
            
        serialPort.notifyOnDataAvailable(true);
            
        
        // creates an inputstream for reading data 
        try 
        {
            input = serialPort.getInputStream();
        } catch (IOException ex) 
        {
            System.out.println(ex.toString());
        }
    }
    
    /**
     * Handles event on the serial port. 
     * Checks if there is data coming in from the serial port.
     * Reads incoming data and saves it to an byte array.
     * Then checks if the message received contains the correct order of startbyte 
     * and stopbyte and number of received bytes.
     * 
     * @param oEvent
     */
    
    @Override
    public synchronized void serialEvent(SerialPortEvent oEvent)
    {
        // check if the event is data being received on the serial port
        if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE)
        {
            try 
            {
                // checks if there is data coming in on the serialport
                if(input.available() >= 0)
                {
                    try
                    {
                        // saving the input data to an byte array
                        input.read(inputData,0,nrOfExpectedInputBytes);                         
                    }
                    
                    catch (IOException e)
                    {
                        System.out.println("failed reading data" + e.getMessage());
                    }
                }
                
                // check if the received message has the correct startbyte, 
                // stopbyte and number of bytes
                if((inputData[firstByte] == 'a') && (inputData[lastByte] == 's'))
                {
                    
                    // save the correct data to the acceptedData variable, now
                    // available for being read.
                    this.acceptedData = inputData;
                    
                    // reset the inputData variable for avoiding trouble when 
                    // next message is being received
                    this.inputData = null;
                    
                    // notify all the listeners of data available for reading
                    this.notifyListeners();
                }
                        
            } catch (IOException ex) 
            {
                System.out.println(ex.toString());
            }
        }
    }
    
    
    /**
     * notify all listeners of data now available for reading
     */
    public void notifyListeners()
    {
        for(SerialInputListener listener : listeners)
        {
            listener.serialDataAvailable();
        }
    }
        
    /**
     * return the data received from the serial communication with the Arduino
     * @return the data that has been accepted from the Arduino
     */ 
    public byte[] getSerialData()
    {
        // return the accepted data.
        return this.acceptedData;
    }
}

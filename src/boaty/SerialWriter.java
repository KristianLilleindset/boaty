/*
 * This class is responsible for sending data from the Odroid to the Arduino
 */
package boaty;

import gnu.io.SerialPort;
import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author kristianandrelilleindset
 */
public class SerialWriter implements Runnable
{
    // variable holding an instance of the serialport
    private final SerialPort serialPort;
    
    // variable holding the output
    private static OutputStream output;
    
    // variable holding the data to be sent to the Arduino
    private byte[] dataToBeSent;
    
    // variable holding the number of the first byte being sent to the Arduino
    private final int startByteNr = 0;
    
    // variable holding the number of which expected to be the last byte in a 
    // message sent to the Arduino
    private final int stopByteNr = 13;
    
    
    /**
     * creating an instance of a SerialWriter
     * 
     * @param serialPort port found which is connected to the Arduino
     */
    public SerialWriter(SerialPort serialPort)
    {
        this.serialPort = serialPort;
        
         // run the initialization of the serialwiter
        this.initialize();
    }
    
    /**
     * 
     */
    private void initialize()
    {
        // create an outputstream for sending data
        try 
        {
            this.output = this.serialPort.getOutputStream();
        } catch (IOException ex) 
        {
            System.out.println(ex.toString());
        }
        
        // setting the start and stopbytes of the data to be sent
        // making it easy for the Arduino to reecognize if the message
        // is at the beginning when it starts to receive.
        
        this.dataToBeSent[startByteNr] = 'o';   // in byte format: 01101111
        
        this.dataToBeSent[stopByteNr] = 's';    // in byte format: 01110011
    }

    /**
     * 
     */
    @Override
    public void run() 
    {
        
    }
    /**
     * sends the data received from the function call
     * 
     * @param sendData - the data to be sent
     */
    
    public synchronized void sendData(byte[] sendData)
    {
        // adding the data to be sent in between the known startbytes and stopbytes 
        for(int byteNr = 1; byteNr < stopByteNr; byteNr++)
        {
            this.dataToBeSent[byteNr] = sendData[byteNr--];
        }
        
        
        try 
        {
            this.output.write(dataToBeSent);
        } catch (IOException ex) 
        {
            System.out.println(ex.toString());
        }
    }
    
}

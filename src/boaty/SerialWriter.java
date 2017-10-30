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
public class SerialWriter implements Runnable, CalculationListener
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
    
    // variable holding the calcualtion for enabling reading new calculations
    private final DataCalculation calculator;
    
    // variable holding info about if new calculation is ready for sending
    private boolean dataAvailable;
    
    
    /**
     * creating an instance of a SerialWriter
     * 
     * @param serialPort port found which is connected to the Arduino
     * @param calculator
     */
    public SerialWriter(SerialPort serialPort, DataCalculation calculator)
    {
        this.serialPort = serialPort;
        
        this.calculator = calculator;
        
         // run the initialization of the serialwiter
        this.initialize();
        
        
        this.calculator.addListener(this);
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

    
    
    /**
     * Getting notyfied on data available from the calculation
     */
    @Override
    public void calculationsReady() 
    {
        this.dataAvailable = true;
    }
    
    
    /**
     * 
     */
    @Override
    public void run() 
    {
        // check if there is new data available from the calculation
        if(dataAvailable)
        {
            // get the calculated data from the calculator and send it
            // with the sendData method.
            this.sendData(this.calculator.getCalculatedData());
            
            // reset the flag telling if data is available
            this.dataAvailable = false;
        }
    }
}

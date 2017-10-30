  /*
 * This is the super class for the classes doing the calculations for sending data to the boat
 * 
 */
package boaty;

import java.util.ArrayList;



/**
 *
 * @author kristianandrelilleindset
 */
public class DataCalculation implements SerialInputListener, Runnable
{
    
    private final SerialReader reader;
    private byte[] onboardData; 
    
    private final ImageProcessing imPro;
    private String cameraData;
    
    private boolean serialDataAvailable;
    private boolean cameraDataAvailable;
    
    private final GyroCalculation gyroMovement;
    private final SailingCalculation boatMovement;
    
    private byte[] gyroData;
    private byte[] boatData;
    private byte[] calculatedData;
    
    // arrayList holding all of the listeners interested in the calculations
    private final ArrayList<CalculationListener> listeners;
    
    
    /**
     * 
     * @param reader
     * @param imPro 
     */
    public DataCalculation(SerialReader reader, ImageProcessing imPro)
            {
                this.reader = reader;
                this.imPro = imPro;
                this.gyroMovement = new GyroCalculation();
                this.boatMovement = new SailingCalculation();
                this.listeners = new ArrayList<>();
                
                // add itself as a listener to the reader data.
                this.reader.addListener(this);
            }
    
    
    /**
     * Set the serialDataAvailable flag = true. 
     */
    @Override
    public void serialDataAvailable() 
    {
        this.serialDataAvailable = true;
    }
    
    
    
    
    /**
     * Add a listener to the list of those who are interested in knowing that 
     * new calculations are ready.
     * listener has to implement the CalculationListener interface
     * @param listener to be added to the list
     */
    public void addListener(CalculationListener listener)
    {  
        this.listeners.add(listener);  
    }
    
    
    /**
     * notify all of the listeners interested in knowing that the calculations 
     * are ready for reading
     * listener has to implement the CalculationListener interface
     */
    private void notifyListeners()
    {
        for(CalculationListener listener : this.listeners)
        {
            listener.calculationsReady();
        }
    }
    
    
    /**
     * 
     */
    public void calculate()
    {
        // get new movement data for the gyro installation 
                    this.gyroData = this.gyroMovement.calculate(this.onboardData);
                    
                    // get new movement data for the boat 
                    this.boatData = this.boatMovement.calculate(this.onboardData, this.cameraData);
                    
                    // notify all of the listeners
                    this.notifyListeners();
    }

    /**
     * 
     */
    public void notifyImageProcessSubscribers()
    {
        this.cameraDataAvailable = true;
    }    
    /**
     * return the data that is done calculating
     */
    public byte[] getCalculatedData()
    {
        return this.calculatedData;
    }
    
    /**
     * 
     */
    @Override
    public void run() 
    {
        while(true)
        {
            if(this.serialDataAvailable || this.cameraDataAvailable)
            {
                
                // check if there is new data available from the serialReader
                if(this.serialDataAvailable)
                {
                    // get the data from the serialRader
                    this.onboardData = this.reader.getSerialData();
                    
                    // set the data flag false since the updated data now has been read
                    this.serialDataAvailable = false;
                }
                
                // check if there is updated data from the image processing
                if(this.cameraDataAvailable)
                {
                    // get the data from the image processing
                    this.cameraData = this.imPro.getObjectPlacement();
                    
                    // set the image data flag false since the data now has been read
                    this.cameraDataAvailable = false;                                      
                }
                
                // make new calculation for sending to the Arduino
                this.calculate();
                
                // notify listeners of interested in the data to the Arduino
            }
            else
            {
                // hva skal gjøres i default ? sooooove? 
            }
        }
    }
}

  /*
 * This is the super class for the classes doing the calculations for sending data to the boat
 * 
 */
package boaty;



/**
 *
 * @author kristianandrelilleindset
 */
public class DataCalculation implements SerialInputListener, Runnable
{
    private byte[] data; 
    private final SerialReader reader;
    private boolean serialDataAvailable;
    private boolean cameraDataAvailable;
    
    
    public DataCalculation(SerialReader reader)
            {
                this.reader = reader;
            }

    
    /**
     * 
     */
    @Override
    public void serialDataAvailable() 
    {
        this.serialDataAvailable = true;
    }
    
    
    
    
    public void calculate()
    {
        
    }

    @Override
    public void run() 
    {
        while(true)
        {
            if(serialDataAvailable || cameraDataAvailable)
            {
                
                // check if there is new data available from the serialReader
                if(serialDataAvailable)
                {
                    // get the data from the serialRader
                    this.data = reader.getSerialData();
                    
                    // set the data flag false since the updated data now has been read
                    this.serialDataAvailable = false;
                }
                
                // check if there is updated data from the image processing
                if(cameraDataAvailable)
                {
                    //
                    
                    
                    // set the image data flag false since the data now has been read
                }
            }
            else
            {
                // hva skal gjøres i default ? sooooove? 
            }
        }
    }
}

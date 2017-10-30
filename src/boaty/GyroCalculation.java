/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package boaty;

/**
 *
 * @author kristianandrelilleindset
 */
public class GyroCalculation
{
    private final int pwmSin[] = {128, 147, 166, 185, 203, 221, 238, 243, 248, 251, 
        253, 255, 255, 255, 253, 251, 248, 243, 238, 243, 248, 251, 253, 255, 
        255, 255, 253, 251, 248, 243, 238, 221, 203, 185, 166, 147, 128, 109, 
        90, 71, 53, 35, 18, 13, 8, 5, 3, 1, 1, 1, 3, 5, 8, 13, 18, 13, 8, 5,
        3, 1, 1, 1, 3, 5, 8, 13, 18, 35, 53, 71, 90, 109};
    
    private final int GYRO_UPDATE_RATE = 50; // 50 ms between reading the gyro.
    
    public GyroCalculation()
    {
        
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algoritmofusion.util;

/**
 *
 * @author roi
 */
public class SensorEvent {
    public float[] values;
    public long timestamp;
    public boolean rotacion;
    
    public SensorEvent(float[] values, long timestamp,boolean rotacion) {
        this.values = values;
        this.timestamp = timestamp;
        this.rotacion = rotacion;
    }
    
}

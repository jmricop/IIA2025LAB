/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Port;

import common.Message;
import common.Slot;

/**
 *
 * @author apolo
 */
public abstract class Port {
    
    protected Slot buffer;
    
    public abstract Message read();
    public abstract void write(Message msg);

    public Port(Slot buffer) {
        this.buffer = buffer;
    }

    public Slot getBuffer() {
        return buffer;
    }

    public void setBuffer(Slot buffer) {
        this.buffer = buffer;
    }
    
    
            
    
}

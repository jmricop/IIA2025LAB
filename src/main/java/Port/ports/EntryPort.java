/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Port.ports;

import Port.Port;
import common.Message;
import common.Slot;

/**
 *
 * @author apolo
 */
public class EntryPort extends Port {

    public EntryPort(Slot buffer) {
        super(buffer);
    }

    @Override
    public Message read() {
       
        return null;
    
    }

    @Override
    public void write(Message msg) {
        
        this.buffer.addMessage(msg);
    
    }
    
}

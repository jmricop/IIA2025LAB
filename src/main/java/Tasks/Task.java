/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Tasks;

import common.Message;
import common.Slot;
import java.util.ArrayList;

/**
 *
 * @author apolo
 */
public abstract class Task {
    
    private ArrayList<Slot> entrySlot;
    private ArrayList<Slot> exitSlot;
    private taskEnum type;
    
    public abstract void action();
    
    public Task(taskEnum t, ArrayList<Slot> es, ArrayList<Slot> exsl) {
        type = t;
        entrySlot = es;
        exitSlot = exsl;
    }

    public boolean isEmpty(int numE) {
        return entrySlot.get(numE).isEmpty();
    }

    public Message getEntryMessage(int numE) {
        return entrySlot.get(numE).getFirstMessage();
    }

    public void setMensajeSalida(Message msg, int numS) {
        exitSlot.get(numS).addMessage(msg);

    }

    public int getNSalidas() {
        return exitSlot.size();
    }

    public ArrayList<Slot> getslotsSalida() {
        return exitSlot;
    }
    
            
    
}

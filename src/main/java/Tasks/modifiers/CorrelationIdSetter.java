/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Tasks.modifiers;

import Tasks.Task;
import Tasks.taskEnum;
import common.Message;
import common.Slot;
import java.util.ArrayList;

/**
 *
 * @author apolo
 */
public class CorrelationIdSetter extends Task {

    private int correlatorId;

    public CorrelationIdSetter(taskEnum t, ArrayList<Slot> entrySlots, ArrayList<Slot> exitSlots) {
        super(t, entrySlots, exitSlots);
        correlatorId=1;
    }
    
    
    @Override
    public void action() {
        if (!isEmpty(0)) {
            Message mensaje = getEntryMessage(0);
            
            correlatorId++;
            mensaje.setcorrelatorId(correlatorId);
            setMensajeSalida(mensaje, 0);

        } else {
            System.out.println("CorrelatorIdSetter: No hay mensajes en la cola de entrada");
        }
    }

    @Override
    public void mock() {
        System.out.println("Correlator ID asignado: " + correlatorId);
    }
    
}

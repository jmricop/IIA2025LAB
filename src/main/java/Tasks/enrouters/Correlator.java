/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Tasks.enrouters;

import Tasks.Task;
import Tasks.taskEnum;
import common.Message;
import common.Slot;
import java.util.ArrayList;

/**
 *
 * @author apolo
 */
public class Correlator extends Task {

    public Correlator(taskEnum t, ArrayList<Slot> entrySlots, ArrayList<Slot> exitSlots) {
        super(t, entrySlots, exitSlots);
    }

    @Override
    public void action() {

        ArrayList<Slot> entradas = getslotsEntrada();
        ArrayList<Slot> salidas = getslotsSalida();

        int index = 0;
        boolean founded = false;

        // Lista base del primer slot
        ArrayList<Message> baseList = new ArrayList<>(entradas.get(0).getMessages());

        while (!founded && index < baseList.size()) {

            Message msg = baseList.get(index);

            try {
                int clave = msg.getCorrelatorId();

                // lista temporal
                ArrayList<Message> aux = new ArrayList<>();
                aux.add(msg);

                // buscar coincidencias en los otros slots
                for (int i = 1; i < entradas.size(); i++) {

                    Message encontrado = null;

                    for (Message m : new ArrayList<>(entradas.get(i).getMessages())) {

                        if (m.getCorrelatorId() == clave) {
                            encontrado = m;
                            break;
                        }
                    }

                    if (encontrado != null) {
                        aux.add(encontrado);
                    } else {
                        break;  // No hay match → no hay correlación
                    }
                }

                // correlación completa: todos los slots tienen su mensaje
                if (aux.size() == entradas.size()) {
                    founded = true;

                    for (int i = 0; i < aux.size(); i++) {
                        Message m = aux.get(i);

                        // remover del slot de entrada
                        entradas.get(i).getMessages().remove(m);

                        // enviar a salida correspondiente
                        salidas.get(i).addMessage(m);
                    }
                }

                index++;

            } catch (Exception e) {
                System.out.println("Error en correlator por correlatorId: " + e.getMessage());
            }
        }
    }

    @Override
    public void mock() {
    }

}

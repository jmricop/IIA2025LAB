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

public class Merger extends Task{

   public Merger(taskEnum t, ArrayList<Slot> se, ArrayList<Slot> sl) {
        // 1. Llama al constructor del "Padre" (Task)
        super(t, se, sl);

        // 2. Comprobación de seguridad: Un Merger solo tiene 1 salida.
        if (sl.size() != 1) {
            throw new IllegalArgumentException("Merger debe tener exactamente 1 buzón de salida.");
        }
        // (Puede tener múltiples entradas, así que no comprobamos 'se')
    }
   
   
   @Override
    public void action() {
        
        // 1. Pregunta al "Padre" cuántos buzones de entrada tiene
        int numBuzonesEntrada = getNumEntrySlots();

        // 2. Itera por CADA buzón de entrada (ej. 0, 1, 2...)
        for (int i = 0; i < numBuzonesEntrada; i++) {
            
            // 3. Comprueba si el buzón 'i' tiene mensajes
            if (!isEmpty(i)) {
                
                // 4. Coge el primer mensaje del buzón 'i'
                Message mensaje = getEntryMessage(i); 
                
                // 5. Lo pone en el ÚNICO buzón de salida (el buzón 0)
                setMensajeSalida(mensaje, 0);
                
                // 6. ¡NO hay 'break'! 
                // El bucle continúa para comprobar el siguiente buzón de entrada
                // (por eso movió 2 mensajes en la primera ejecución de la prueba)
            }
        }
    }

    @Override
    public void mock() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
}

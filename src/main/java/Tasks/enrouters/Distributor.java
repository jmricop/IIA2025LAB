package Tasks.enrouters;

import Tasks.Task;
import Tasks.taskEnum;
import common.Message;
import common.Slot;
import java.util.ArrayList;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

public class Distributor extends Task {

    private Message mensaje;
    private String      tagALeer;
    private String[]    Condiciones;
    private int            NCondiciones;

    public Distributor(taskEnum t, ArrayList<Slot> se, ArrayList<Slot> sl, String[] Condic, int NumCond, String tagALeer) {
        super(t, se, sl);
        Condiciones = new String[NumCond];
        
        for(int i = 0; i < NumCond; i++) {
            Condiciones[i] = Condic[i];
        }
        NCondiciones = NumCond;
        this.tagALeer = tagALeer;
    }

    public void setAtributos(String[] Con, int num, String tag) {
        this.Condiciones = Con;
        this.NCondiciones = num;
        this.tagALeer = tag;
    }

    public void procesar() {
        while (!this.isEmpty(0)) {
            //Obtiene el mensaje
            mensaje = getEntryMessage(0);

            XPathFactory xfactory = XPathFactory.newInstance();
            XPath xpath = xfactory.newXPath();

            for (int i = 0; i < getNSalidas(); i++) {
                try {
                    //String expression = "boolean(" + tagALeer + "[text()='" + Condiciones[i] + "'])";
                    String expression = "boolean(//" + tagALeer + "[text()='" + Condiciones[i] + "'])";

                    boolean result = (boolean) xpath.evaluate(expression, mensaje.getDocument(), XPathConstants.BOOLEAN);
                    
                    if (result) {
                        System.out.println("Sacando mensaje por salida " + i + " | Condicion: " + Condiciones[i]);
                        setMensajeSalida(mensaje, i);
                    }
                } catch (XPathExpressionException ex) {
                    System.out.println("Fallo al evaluar la condicion de la salida " + i);
                }
            }
        }
    }

    @Override
    public void action() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void mock() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
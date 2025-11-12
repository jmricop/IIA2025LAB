/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Tasks.transformators;

import Tasks.Task;
import Tasks.taskEnum;
import common.Slot;
import java.util.ArrayList;



/**
 *
 * @author apolo
 */
public class Splitter extends Task {

    private String xPathExpression;
    private String idXML;

    public Splitter(taskEnum t, ArrayList<Slot> es, ArrayList<Slot> exsl) {
        super(t, es, exsl);
    }

    @Override
    public void action() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void mock() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    
    
}

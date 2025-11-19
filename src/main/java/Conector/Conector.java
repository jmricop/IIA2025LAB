/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Conector;

import Port.Port;

/**
 *
 * @author apolo
 */
public abstract class Conector {
    
    protected Port port;
    
    public abstract void action();

    public Conector(Port port) {
        this.port = port;
    }
    
}

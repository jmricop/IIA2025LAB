/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package common;

/**
 *
 * @author josec
 */
public class IdUnico {

    private static final IdUnico INSTANCE = new IdUnico();

    private int contador;

    private IdUnico() {
        this.contador = 0;
    }

    public static IdUnico getInstance() {
        return INSTANCE;
    }

    public int getNextIdDocument() {
        return ++this.contador;
    }
}

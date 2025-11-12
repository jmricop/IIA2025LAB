/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package common;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author josec
 */
public class Diccionario {

    private static final Diccionario INSTANCE = new Diccionario();

    private Map<Integer, ValoresDiccionario> mapa;

    private Diccionario() {
        this.mapa = new HashMap<>();
    }

    public static Diccionario getInstance() {
        return INSTANCE;
    }

    public void put(int idDocument, ValoresDiccionario valores) {
        this.mapa.put(idDocument, valores);
    }

    public ValoresDiccionario get(int idDocument) {
        return this.mapa.get(idDocument);
    }

    public ValoresDiccionario remove(int idDocument) {
        return this.mapa.remove(idDocument);
    }
}

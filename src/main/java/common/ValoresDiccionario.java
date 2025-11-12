/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package common;

import org.w3c.dom.Document;

/**
 *
 * @author josec
 */
public class ValoresDiccionario {

    private Document caparazon;
    private String xPathDondeInsertar;

    public ValoresDiccionario(String xPathDondeInsertar, Document caparazon) {
        this.xPathDondeInsertar = xPathDondeInsertar;
        this.caparazon = caparazon;
    }

    public Document getCaparazon() {
        return caparazon;
    }

    public String getXPathDondeInsertar() {
        return xPathDondeInsertar;
    }
}

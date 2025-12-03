package Conector.connectors;

import Conector.Conector;
import Port.Port;
import common.Message;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import javax.xml.parsers.DocumentBuilderFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ConectorTelegram extends Conector {

    private final String botToken;
    private long lastUpdateId = 0; // Para no leer el mismo mensaje dos veces
    private final HttpClient httpClient;

    public ConectorTelegram(Port port, String botToken) {
        super(port);
        this.botToken = botToken;
        this.httpClient = HttpClient.newHttpClient();
    }

    @Override
    public void action() {
        try {
            // 1. Construir la petición a Telegram (Polling)
            // offset = lastUpdateId + 1 confirma que ya leímos los anteriores
            String url = "https://api.telegram.org/bot" + botToken + "/getUpdates?offset=" + (lastUpdateId + 1);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            // 2. Enviar petición
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JSONObject json = new JSONObject(response.body());
                JSONArray result = json.getJSONArray("result");

                // 3. Procesar cada mensaje nuevo
                for (int i = 0; i < result.length(); i++) {
                    JSONObject update = result.getJSONObject(i);
                    lastUpdateId = update.getLong("update_id"); // Actualizamos el ID

                    if (update.has("message")) {
                        JSONObject message = update.getJSONObject("message");
                        if (message.has("text")) {
                            String texto = message.getString("text");
                            String usuario = message.getJSONObject("from").getString("first_name");
                            
                            System.out.println("--- TELEGRAM: Mensaje recibido de " + usuario + ": " + texto);
                            
                            // 4. Transformar Texto a XML (Normalización)
                            // Esperamos formato: "producto tipo" (ej: "cafe hot")
                            Message msgInterno = crearMensajeDesdeTexto(texto, usuario);
                            
                            if (msgInterno != null) {
                                this.port.write(msgInterno);
                                System.out.println("--- TELEGRAM: Comanda inyectada al sistema ---");
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error en ConnectorTelegram: " + e.getMessage());
        }
    }

    private Message crearMensajeDesdeTexto(String texto, String usuario) {
        try {
            // Parseo simple: separamos por espacio. Ej: "cafe hot"
            String[] partes = texto.split(" ");
            String producto = partes[0];
            String tipo = (partes.length > 1) ? partes[1] : "hot"; // Default hot

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            Document doc = dbf.newDocumentBuilder().newDocument();

            // Estructura XML compatible con tu sistema
            Element root = doc.createElement("cafe_order");
            doc.appendChild(root);

            Element orderId = doc.createElement("order_id");
            orderId.setTextContent("TG-" + System.currentTimeMillis()); // ID único
            root.appendChild(orderId);

            Element drinks = doc.createElement("drinks");
            root.appendChild(drinks);

            Element drink = doc.createElement("drink");
            drinks.appendChild(drink);

            Element name = doc.createElement("name");
            name.setTextContent(producto);
            drink.appendChild(name);

            Element type = doc.createElement("type");
            type.setTextContent(tipo);
            drink.appendChild(type);

            return new Message(doc, 1); // ID Lote 1 por defecto

        } catch (Exception e) {
            System.err.println("No se pudo convertir el mensaje de Telegram a XML");
            return null;
        }
    }
}
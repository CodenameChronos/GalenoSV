package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control;

import jakarta.json.Json;
import jakarta.json.JsonException;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Decodifica el campo observaciones, equivalente a observaciones_service.js.
 * Formato de cada bloque: ##PASO:{uuid}##{json}##/PASO##
 *
 * @author kardia
 */
public final class ObservacionesCodec {

    private static final Pattern BLOQUE = Pattern.compile(
            "##PASO:([0-9a-fA-F-]{36})##([^#]*)##/PASO##");

    public record Paso(String idPaso, JsonObject datos) {}

    private ObservacionesCodec() {}

    /** Equivalente a leerTodos(): omite los bloques con JSON dañado. */
    public static List<Paso> leerTodos(String texto) {
        List<Paso> salida = new ArrayList<>();
        if (texto == null) {
            return salida;
        }
        Matcher m = BLOQUE.matcher(texto);
        while (m.find()) {
            try (JsonReader r = Json.createReader(new StringReader(m.group(2)))) {
                salida.add(new Paso(m.group(1).toLowerCase(), r.readObject()));
            } catch (JsonException | IllegalStateException e) {
                // bloque dañado: se ignora
            }
        }
        return salida;
    }

    /** Texto fuera de los bloques (la observación libre). */
    public static String textoLibre(String texto) {
        if (texto == null) {
            return "";
        }
        return BLOQUE.matcher(texto).replaceAll("").trim();
    }

    /** Valor de "notas" del bloque, tolerante a null o tipos distintos de texto. */
    public static String notas(JsonObject datos) {
        JsonValue v = datos.get("notas");
        return (v instanceof JsonString s) ? s.getString().trim() : "";
    }
}
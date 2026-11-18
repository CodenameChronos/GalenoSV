package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Consulta;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ConsultaProcedimiento;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Documento;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Persona;

@ApplicationScoped
public class TicketPdfBean {

    // Ancho estándar de rollo térmico 80 mm (226.77 pt) x 150 mm (425 pt)
    private static final Rectangle TAMANO_TICKET = new Rectangle(226.77f, 425f);
    private static final String SEP_DOBLE = "====================================";
    private static final String SEP_SIMPLE = "------------------------------------";

    @PersistenceContext(unitName = "Galeno-PU")
    EntityManager em;

    public byte[] generarTicketCita(Consulta consulta) {
        if (consulta == null) {
            throw new IllegalArgumentException("La consulta no puede ser nula");
        }

        SimpleDateFormat fmtFecha = new SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("es-SV"));
        SimpleDateFormat fmtHora = new SimpleDateFormat("hh:mm a", Locale.US);
        SimpleDateFormat fmtEmision = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.US);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(TAMANO_TICKET, 12f, 12f, 14f, 14f);
            PdfWriter.getInstance(document, baos);
            document.open();

            Font fontClinica = FontFactory.getFont(FontFactory.COURIER_BOLD, 10f, Color.BLACK);
            Font fontSeccion = FontFactory.getFont(FontFactory.COURIER_BOLD, 8.5f, Color.BLACK);
            Font fontTexto = FontFactory.getFont(FontFactory.COURIER, 8.5f, Color.BLACK);
            Font fontSep = FontFactory.getFont(FontFactory.COURIER, 8f, Color.BLACK);
            Font fontPie = FontFactory.getFont(FontFactory.COURIER, 8f, Color.BLACK);

            // 1. Encabezado: solo nombre de la clínica y título del comprobante
            String nombreClinica = "CLÍNICA MÉDICA OFIUCO SV";
            Persona persona = null;

            if (consulta.getIdPersonaRol() != null) {
                if (consulta.getIdPersonaRol().getIdClinica() != null
                        && consulta.getIdPersonaRol().getIdClinica().getNombre() != null
                        && !consulta.getIdPersonaRol().getIdClinica().getNombre().isBlank()) {
                    nombreClinica = consulta.getIdPersonaRol().getIdClinica().getNombre().toUpperCase();
                }
                persona = consulta.getIdPersonaRol().getIdPersona();
            }

            document.add(parrafoCentrado(nombreClinica, fontClinica, 2f));
            document.add(parrafoCentrado(SEP_DOBLE, fontSep, 1f));
            document.add(parrafoCentrado("COMPROBANTE DE CITA MÉDICA", fontSeccion, 1f));
            document.add(parrafoCentrado(SEP_SIMPLE, fontSep, 2f));

            // 2. Identificador de ticket y emisión
            String codigoTicket = formatearCodigoTicket(consulta.getIdConsulta());
            document.add(lineaClaveValor("TICKET No.", codigoTicket, fontSeccion, fontTexto));
            document.add(lineaClaveValor("EMISIÓN", fmtEmision.format(new Date()), fontTexto, fontTexto));
            document.add(parrafoCentrado(SEP_SIMPLE, fontSep, 2f));

            // 3. Datos del paciente y documento
            document.add(parrafoIzquierda("PACIENTE", fontSeccion, 1f));
            String nombrePaciente = (persona != null && !persona.getNombreCompleto().isBlank())
                    ? persona.getNombreCompleto()
                    : "Paciente no especificado";
            document.add(parrafoIzquierda(nombrePaciente, fontTexto, 1f));

            Documento doc = obtenerDocumentoPrincipal(persona);
            if (doc != null) {
                String tipoDoc = (doc.getIdTipoDocumento() != null && doc.getIdTipoDocumento().getNombre() != null)
                        ? doc.getIdTipoDocumento().getNombre()
                        : "DOC";
                String numDoc = doc.getValor() != null ? doc.getValor() : "-";
                document.add(lineaClaveValor(tipoDoc, numDoc, fontTexto, fontTexto));
            }
            document.add(parrafoCentrado(SEP_SIMPLE, fontSep, 2f));

            // 4. Datos de la cita
            document.add(parrafoIzquierda("DATOS DE LA CITA", fontSeccion, 1f));
            String fechaCita = consulta.getFechaInicio() != null ? fmtFecha.format(consulta.getFechaInicio()) : "-";
            String horaCita = consulta.getFechaInicio() != null ? fmtHora.format(consulta.getFechaInicio()) : "-";
            String estadoCita = (consulta.getFechaFin() != null) ? "FINALIZADA" : "PROGRAMADA";

            document.add(lineaClaveValor("FECHA CITA", fechaCita, fontTexto, fontTexto));
            document.add(lineaClaveValor("HORA", horaCita, fontTexto, fontTexto));
            document.add(lineaClaveValor("ESTADO", estadoCita, fontTexto, fontTexto));
            document.add(parrafoCentrado(SEP_SIMPLE, fontSep, 2f));

            // 5. Procedimientos / Indicaciones
            document.add(parrafoIzquierda("PROCEDIMIENTOS / INDICACIONES", fontSeccion, 2f));

            List<ConsultaProcedimiento> procedimientos = obtenerProcedimientos(consulta.getIdConsulta());
            boolean tieneContenido = false;

            for (ConsultaProcedimiento cp : procedimientos) {
                String nombreProc = (cp.getIdProcedimiento() != null && cp.getIdProcedimiento().getNombre() != null)
                        ? cp.getIdProcedimiento().getNombre()
                        : "Procedimiento";
                document.add(parrafoIzquierda("* " + nombreProc, fontTexto, 1f));
                tieneContenido = true;

                String obsCp = cp.getObservaciones();
                for (ObservacionesCodec.Paso paso : ObservacionesCodec.leerTodos(obsCp)) {
                    String nota = ObservacionesCodec.notas(paso.datos());
                    if (!nota.isEmpty()) {
                        document.add(parrafoIzquierda("  - " + nota, fontTexto, 1f));
                    }
                }
                String libreCp = ObservacionesCodec.textoLibre(obsCp);
                if (!libreCp.isEmpty()) {
                    document.add(parrafoIzquierda("  Obs: " + libreCp, fontTexto, 1f));
                }
            }

            if (consulta.getObservaciones() != null && !consulta.getObservaciones().isBlank()) {
                document.add(parrafoIzquierda("Obs: " + consulta.getObservaciones(), fontTexto, 1f));
                tieneContenido = true;
            }

            if (!tieneContenido) {
                document.add(parrafoIzquierda("* Sin procedimientos registrados.", fontTexto, 1f));
            }

            // 6. Cierre minimalista
            document.add(parrafoCentrado(SEP_DOBLE, fontSep, 6f));
            document.add(parrafoCentrado("Presente este ticket en recepción", fontPie, 0f));

            document.close();
            return baos.toByteArray();
        } catch (DocumentException | IOException e) {
            throw new IllegalStateException("Error al generar el PDF del ticket de cita", e);
        }
    }

    private List<ConsultaProcedimiento> obtenerProcedimientos(UUID idConsulta) {
        if (idConsulta == null || em == null) {
            return Collections.emptyList();
        }
        try {
            return em.createNamedQuery("ConsultaProcedimiento.listarPorConsulta", ConsultaProcedimiento.class)
                    .setParameter("consulta", idConsulta)
                    .getResultList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private Documento obtenerDocumentoPrincipal(Persona persona) {
        if (persona == null || persona.getIdPersona() == null || em == null) {
            return null;
        }
        try {
            List<Documento> docs = em.createQuery(
                    "SELECT d FROM Documento d JOIN FETCH d.idTipoDocumento WHERE d.idPersona.idPersona = :idPersona",
                    Documento.class)
                    .setParameter("idPersona", persona.getIdPersona())
                    .setMaxResults(1)
                    .getResultList();
            return docs.isEmpty() ? null : docs.get(0);
        } catch (Exception e) {
            return null;
        }
    }

    private String formatearCodigoTicket(UUID idConsulta) {
        if (idConsulta == null) {
            return "CITA-00000000";
        }
        return "CITA-" + idConsulta.toString().replace("-", "").substring(0, 8).toUpperCase();
    }

    private Paragraph lineaClaveValor(String clave, String valor, Font fClave, Font fValor) {
        String etiquetaAlineada = String.format("%-11s: ", clave);
        Paragraph p = new Paragraph();
        p.setLeading(11f);
        p.setSpacingAfter(1f);
        p.add(new com.lowagie.text.Chunk(etiquetaAlineada, fClave));
        p.add(new com.lowagie.text.Chunk(valor != null ? valor : "-", fValor));
        return p;
    }

    private Paragraph parrafoCentrado(String texto, Font font, float spacingAfter) {
        Paragraph p = new Paragraph(texto, font);
        p.setAlignment(Element.ALIGN_CENTER);
        p.setLeading(11f);
        p.setSpacingAfter(spacingAfter);
        return p;
    }

    private Paragraph parrafoIzquierda(String texto, Font font, float spacingAfter) {
        Paragraph p = new Paragraph(texto, font);
        p.setAlignment(Element.ALIGN_LEFT);
        p.setLeading(11f);
        p.setSpacingAfter(spacingAfter);
        return p;
    }
}

package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import jakarta.enterprise.context.ApplicationScoped;
import java.awt.Color;
import java.io.ByteArrayOutputStream;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;


import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Consulta;

@ApplicationScoped
public class TicketPdfBean {

    public byte[] generarTicketCita(Consulta consulta) {
        if (consulta == null) {
            throw new IllegalArgumentException("La consulta no puede ser nula");
        }

        SimpleDateFormat fmtFecha = new SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("es-SV"));
        SimpleDateFormat fmtHora = new SimpleDateFormat("hh:mm a", Locale.US);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A6, 20, 20, 20, 20);
            PdfWriter.getInstance(document, baos);
            document.open();

            Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, new Color(88, 37, 147));
            Font fontSubtitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.DARK_GRAY);
            Font fontLabel = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.BLACK);
            Font fontValor = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.BLACK);

            Paragraph encabezado = new Paragraph("OfiucoSV - Ticket de Cita Médica", fontTitulo);
            encabezado.setAlignment(Element.ALIGN_CENTER);
            encabezado.setSpacingAfter(8f);
            document.add(encabezado);

            PdfPTable tabla = new PdfPTable(2);
            tabla.setWidthPercentage(100);
            tabla.setWidths(new float[]{38f, 62f});
            tabla.setSpacingBefore(4f);
            tabla.setSpacingAfter(8f);

            agregarFila(tabla, "Código Cita:", String.valueOf(consulta.getIdConsulta()), fontLabel, fontValor);

            if (consulta.getFechaInicio() != null) {
                agregarFila(tabla, "Fecha:", fmtFecha.format(consulta.getFechaInicio()), fontLabel, fontValor);
                agregarFila(tabla, "Hora:", fmtHora.format(consulta.getFechaInicio()), fontLabel, fontValor);
            }

            String nombreClinica = "Clínica asignada";
            String nombrePaciente = null;

            if (consulta.getIdPersonaRol() != null) {
                if (consulta.getIdPersonaRol().getIdClinica() != null
                        && consulta.getIdPersonaRol().getIdClinica().getNombre() != null) {
                    nombreClinica = consulta.getIdPersonaRol().getIdClinica().getNombre();
                }
                if (consulta.getIdPersonaRol().getIdPersona() != null) {
                    String completo = consulta.getIdPersonaRol().getIdPersona().getNombreCompleto();
                    if (completo != null && !completo.isBlank()) {
                        nombrePaciente = completo;
                    }
                }
            }

            agregarFila(tabla, "Clínica:", nombreClinica, fontLabel, fontValor);
            if (nombrePaciente != null) {
                agregarFila(tabla, "Paciente:", nombrePaciente, fontLabel, fontValor);
            }

            document.add(tabla);

            Paragraph tituloObs = new Paragraph("Procedimientos / Indicaciones:", fontSubtitulo);
            tituloObs.setSpacingAfter(4f);
            document.add(tituloObs);

            String observaciones = (consulta.getObservaciones() != null && !consulta.getObservaciones().isBlank())
                    ? consulta.getObservaciones()
                    : "Sin procedimientos registrados.";
            document.add(new Paragraph(observaciones, fontValor));

            document.close();
            return baos.toByteArray();
        } catch (DocumentException | IOException e) {
            throw new IllegalStateException("Error al generar el PDF del ticket de cita", e);
        }
    }

    private void agregarFila(PdfPTable tabla, String etiqueta, String valor, Font fLabel, Font fValor) {
        PdfPCell c1 = new PdfPCell(new Phrase(etiqueta, fLabel));
        c1.setBorderWidth(0.5f);
        c1.setPadding(4f);
        c1.setBackgroundColor(new Color(222, 230, 241));

        PdfPCell c2 = new PdfPCell(new Phrase(valor != null ? valor : "-", fValor));
        c2.setBorderWidth(0.5f);
        c2.setPadding(4f);

        tabla.addCell(c1);
        tabla.addCell(c2);
    }
}
package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ExamenTipoExamenService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.TipoExamenService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Examen;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ExamenTipoExamen;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.TipoExamen;

@Named
@ViewScoped
public class ExamenTipoExamenArbolModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private ExamenTipoExamenModel examenModel;

    @Inject
    private TipoExamenService tipoExamenService;

    @Inject
    private ExamenTipoExamenService examenTipoExamenService;

    @Inject
    private ExamenTipoExamenService service;

    private List<TipoExamen> tiposExamenForm = new ArrayList<>();

    private TipoExamen tipoExamenAgregar;
    private TipoExamen tipoExamenSeleccionado;

    private java.util.UUID examenCargado;

    public List<TipoExamen> getTiposExamenForm() {
        sincronizarConExamen();
        return tiposExamenForm;
    }

    public TipoExamen getTipoExamenAgregar() {
        return tipoExamenAgregar;
    }

    public void setTipoExamenAgregar(TipoExamen tipoExamenAgregar) {
        this.tipoExamenAgregar = tipoExamenAgregar;
    }

    public TipoExamen getTipoExamenSeleccionado() {
        return tipoExamenSeleccionado;
    }

    public void setTipoExamenSeleccionado(TipoExamen tipoExamenSeleccionado) {
        this.tipoExamenSeleccionado = tipoExamenSeleccionado;
    }

    public void alEntrarPestana() {
        cargarTipos();
    }

    private void sincronizarConExamen() {
        Examen examen = examenModel.getRegistroActual();

        java.util.UUID idActual = examen != null
                ? examen.getIdExamen()
                : null;

        if (!java.util.Objects.equals(idActual, examenCargado)) {
            cargarTipos();
        }
    }

    private void cargarTipos() {
        Examen examen = examenModel.getRegistroActual();

        tiposExamenForm.clear();
        tipoExamenAgregar = null;
        tipoExamenSeleccionado = null;

        if (examen == null || examen.getIdExamen() == null) {
            examenCargado = null;
            return;
        }

        List<ExamenTipoExamen> relaciones =
                examenTipoExamenService.findByIdExamen(
                        examen.getIdExamen(), 0, Integer.MAX_VALUE);

        for (ExamenTipoExamen relacion : relaciones) {
            if (relacion.getIdTipoExamen() != null) {
                tiposExamenForm.add(relacion.getIdTipoExamen());
            }
        }

        examenCargado = examen.getIdExamen();
    }

    public List<TipoExamen> completarTipoExamen(String texto) {
        List<TipoExamen> encontrados =
                new ArrayList<>(tipoExamenService.buscarPorNombre(texto, 10));

        encontrados.removeAll(tiposExamenForm);

        return encontrados;
    }

    public void agregarTipoExamen() {

        if (tipoExamenAgregar == null) {
            agregarMensaje(
                    FacesMessage.SEVERITY_WARN,
                    "Debe seleccionar un tipo de examen.");
            return;
        }

        if (tiposExamenForm.contains(tipoExamenAgregar)) {
            agregarMensaje(
                    FacesMessage.SEVERITY_WARN,
                    "El tipo de examen ya fue agregado.");
            return;
        }

        tiposExamenForm.add(tipoExamenAgregar);
        tipoExamenAgregar = null;
    }

    public void quitarTipoExamen() {

        if (tipoExamenSeleccionado == null) {
            agregarMensaje(
                    FacesMessage.SEVERITY_WARN,
                    "Debe seleccionar un tipo de examen para quitarlo.");
            return;
        }

        tiposExamenForm.remove(tipoExamenSeleccionado);
        tipoExamenSeleccionado = null;
    }

    public void guardarTipos() {

        Examen examen = examenModel.getRegistroActual();

        if (examen == null || examen.getIdExamen() == null) {
            agregarMensaje(
                    FacesMessage.SEVERITY_WARN,
                    "El examen debe guardarse antes de asignar tipos de examen.");
            return;
        }

        try {
            service.sincronizarTipos(examen, tiposExamenForm);

            agregarMensaje(
                    FacesMessage.SEVERITY_INFO,
                    "Tipos de examen guardados correctamente.");

            cargarTipos();

        } catch (Exception ex) {
            agregarMensaje(
                    FacesMessage.SEVERITY_ERROR,
                    "No se pudieron guardar los tipos de examen.");
        }
    }

    public void limpiar() {
        tiposExamenForm.clear();
        tipoExamenAgregar = null;
        tipoExamenSeleccionado = null;
        examenCargado = null;
    }

    private void agregarMensaje(
            FacesMessage.Severity severidad,
            String texto) {

        FacesContext.getCurrentInstance().addMessage(
                null,
                new FacesMessage(severidad, texto, null));
    }
}
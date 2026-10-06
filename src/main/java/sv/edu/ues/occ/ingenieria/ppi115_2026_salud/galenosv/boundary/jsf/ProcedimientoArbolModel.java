package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.*;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.*;

@Named
@ViewScoped
public class ProcedimientoArbolModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private ProcedimientoModel procedimientoModel;

    @Inject
    private ProcedimientoPasoService pasoDAO;

    @Inject
    private ProcedimientoPasoSecuenciaService secuenciaService;

    @Inject
    private ProcedimientoPasoService service;

    @Inject
    private ProcedimientoPasoExamenService examenPasoService;

    @Inject
    private ExamenService examenService;

    @Inject
    private RolService rolService;

    private TreeNode<ProcedimientoPaso> raiz;

    private List<Rol> roles;

    private final Map<UUID, ProcedimientoPaso> padreDe = new HashMap<>();

    private UUID arbolDeProcedimiento;

    private final Set<UUID> conHijo = new HashSet<>();

    private boolean editandoPaso;
    private boolean modoEdicion;
    private ProcedimientoPaso padreSeleccionado;
    private ProcedimientoPaso pasoEnEdicion;
    private String nombreForm;
    private Rol rolForm;
    private boolean indicaFinForm;
    private List<Examen> examenesForm = new ArrayList<>();
    private Examen examenAgregar;
    private Examen examenSeleccionado;


    public TreeNode<ProcedimientoPaso> getRaiz() {
        sincronizarConRegistroActual();
        return raiz;
    }

    public List<Rol> getRoles() {
        if (roles == null) {
            roles = new ArrayList<>(rolService.findAllActive());
        }
        return roles;
    }

    public boolean isEditandoPaso() {
        sincronizarConRegistroActual();
        return editandoPaso;
    }

    public boolean isModoEdicion() {
        return modoEdicion;
    }

    public String getNombreForm() {
        return nombreForm;
    }

    public void setNombreForm(String nombreForm) {
        this.nombreForm = nombreForm;
    }

    public Rol getRolForm() {
        return rolForm;
    }

    public void setRolForm(Rol rolForm) {
        this.rolForm = rolForm;
    }

    public boolean isIndicaFinForm() {
        return indicaFinForm;
    }

    public void setIndicaFinForm(boolean indicaFinForm) {
        this.indicaFinForm = indicaFinForm;
    }

    public List<Examen> getExamenesForm() {
        return examenesForm;
    }

    public void setExamenesForm(List<Examen> examenesForm) {
        this.examenesForm = examenesForm;
    }

    public Examen getExamenAgregar() {
        return examenAgregar;
    }

    public void setExamenAgregar(Examen examenAgregar) {
        this.examenAgregar = examenAgregar;
    }

    public Examen getExamenSeleccionado() {
        return examenSeleccionado;
    }

    public void setExamenSeleccionado(Examen examenSeleccionado) {
        this.examenSeleccionado = examenSeleccionado;
    }

    public String getIdPasoForm() {
        return pasoEnEdicion != null && pasoEnEdicion.getIdProcedimientoPaso() != null
                ? pasoEnEdicion.getIdProcedimientoPaso().toString()
                : "";
    }

    public String getDependeDeNombre() {
        return padreSeleccionado != null ? padreSeleccionado.getNombre() : "";
    }

    public boolean puedeAgregarHijo(ProcedimientoPaso paso) {
        return !Boolean.TRUE.equals(paso.getIndicaFin())
                && !conHijo.contains(paso.getIdProcedimientoPaso());
    }

    public boolean puedeEliminar(ProcedimientoPaso paso) {
        return !conHijo.contains(paso.getIdProcedimientoPaso());
    }

    private void sincronizarConRegistroActual() {
        if (!Objects.equals(idProcedimientoActual(), arbolDeProcedimiento)) {
            editandoPaso = false;
            construirArbol();
        }
    }

    private UUID idProcedimientoActual() {
        Procedimiento actual = procedimientoModel.getRegistroActual();
        return actual != null ? actual.getIdProcedimiento() : null;
    }

    public void alEntrarPestana() {
        editandoPaso = false;
        construirArbol();
    }

    public void construirArbol() {
        raiz = null;
        padreDe.clear();
        conHijo.clear();
        arbolDeProcedimiento = idProcedimientoActual();
        if (procedimientoModel.getRegistroActual() == null
                || procedimientoModel.getRegistroActual().getIdProcedimiento() == null) {
            return;
        }
        UUID idProcedimiento = procedimientoModel.getRegistroActual().getIdProcedimiento();

        List<ProcedimientoPaso> pasos = pasoDAO.listarPorProcedimiento(idProcedimiento);
        if (pasos.isEmpty()) {
            return;
        }
        List<ProcedimientoPasoSecuencia> bordes = secuenciaService.listarPorProcedimiento(idProcedimiento);

        Map<UUID, ProcedimientoPaso> pasoPorId = new HashMap<>();
        for (ProcedimientoPaso p : pasos) {
            pasoPorId.put(p.getIdProcedimientoPaso(), p);
        }

        Map<UUID, List<ProcedimientoPaso>> hijosPorPadre = new HashMap<>();
        Set<UUID> conPadre = new HashSet<>();
        for (ProcedimientoPasoSecuencia borde : bordes) {
            UUID idHijo = borde.getIdProcedimientoPaso().getIdProcedimientoPaso();
            UUID idPadre = borde.getIdProcedimientoPasoReferencia();
            conPadre.add(idHijo);
            hijosPorPadre.computeIfAbsent(idPadre, k -> new ArrayList<>()).add(pasoPorId.get(idHijo));
        }

        List<ProcedimientoPaso> candidatosRaiz = new ArrayList<>();
        for (ProcedimientoPaso p : pasos) {
            if (!conPadre.contains(p.getIdProcedimientoPaso())) {
                candidatosRaiz.add(p);
            }
        }

        if (candidatosRaiz.isEmpty()) {
            agregarMensaje(FacesMessage.SEVERITY_WARN, "procedimiento.arbol.error.sinRaiz");
            return;
        }
        if (candidatosRaiz.size() > 1) {
            agregarMensaje(FacesMessage.SEVERITY_WARN, "procedimiento.arbol.error.variasRaices");
        }

        ProcedimientoPaso datosRaiz = candidatosRaiz.get(0);

        TreeNode<ProcedimientoPaso> contenedor = new DefaultTreeNode<ProcedimientoPaso>(null, null);
        contenedor.setExpanded(true);

        TreeNode<ProcedimientoPaso> nodoRaiz = new DefaultTreeNode<>(datosRaiz, contenedor);
        nodoRaiz.setExpanded(true);
        construirHijosRecursivo(nodoRaiz, datosRaiz.getIdProcedimientoPaso(), hijosPorPadre);

        raiz = contenedor;
    }

    private void construirHijosRecursivo(TreeNode<ProcedimientoPaso> nodoPadre, UUID idPadre,
                                         Map<UUID, List<ProcedimientoPaso>> hijosPorPadre) {
        List<ProcedimientoPaso> hijos = hijosPorPadre.get(idPadre);
        if (hijos == null) {
            return;
        }
        for (ProcedimientoPaso hijo : hijos) {
            if (hijo == null) {
                continue;
            }
            padreDe.put(hijo.getIdProcedimientoPaso(), nodoPadre.getData());
            conHijo.add(nodoPadre.getData().getIdProcedimientoPaso());
            TreeNode<ProcedimientoPaso> nodoHijo = new DefaultTreeNode<>(hijo, nodoPadre);
            nodoHijo.setExpanded(true);
            construirHijosRecursivo(nodoHijo, hijo.getIdProcedimientoPaso(), hijosPorPadre);
        }
    }

    public void abrirAgregarRaiz() {
        abrirFormulario(false, null, nuevoPaso());
    }

    public void abrirAgregarHijo(ProcedimientoPaso padre) {
        abrirFormulario(false, padre, nuevoPaso());
    }

    public void abrirEditar(ProcedimientoPaso paso) {
        abrirFormulario(true, padreDe.get(paso.getIdProcedimientoPaso()), paso);
    }

    private ProcedimientoPaso nuevoPaso() {
        ProcedimientoPaso nuevo = new ProcedimientoPaso();
        nuevo.setIdProcedimientoPaso(UUID.randomUUID());
        return nuevo;
    }

    private void abrirFormulario(boolean edicion, ProcedimientoPaso padre, ProcedimientoPaso paso) {
        modoEdicion = edicion;
        padreSeleccionado = padre;
        pasoEnEdicion = paso;
        nombreForm = paso.getNombre();
        rolForm = paso.getIdRol();
        indicaFinForm = Boolean.TRUE.equals(paso.getIndicaFin());
        examenesForm = edicion
                ? new ArrayList<>(examenPasoService.listarExamenesDePaso(paso.getIdProcedimientoPaso()))
                : new ArrayList<>();
        examenAgregar = null;
        examenSeleccionado = null;
        editandoPaso = true;
    }

    public void cancelarPaso() {
        editandoPaso = false;
    }

    public void guardarPaso() {
        try {
            pasoEnEdicion.setNombre(nombreForm);
            if (!modoEdicion) {
                pasoEnEdicion.setIdRol(rolForm);
            }
            pasoEnEdicion.setIndicaFin(indicaFinForm);
            if (modoEdicion) {
                service.actualizarPaso(pasoEnEdicion, examenesForm);
            } else if (padreSeleccionado == null) {
                service.crearPasoInicial(pasoEnEdicion, procedimientoModel.getRegistroActual(), examenesForm);
            } else {
                service.crearPasoHijo(pasoEnEdicion, padreSeleccionado, examenesForm);
            }
            editandoPaso = false;
            construirArbol();
            agregarMensaje(FacesMessage.SEVERITY_INFO, "procedimiento.arbol.exito.guardar");
        } catch (ReglaNegocioException ex) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), null));
        } catch (Exception ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
            agregarMensaje(FacesMessage.SEVERITY_ERROR, "procedimiento.arbol.error.guardar");
        }
    }

    public List<Examen> completarExamen(String texto) {
        List<Examen> encontrados = new ArrayList<>(examenService.buscarPorNombre(texto, 10));
        encontrados.removeAll(examenesForm);
        return encontrados;
    }

    /** Agrega a la lista el examen elegido en el autocompletar. */
    public void agregarExamen() {
        if (examenAgregar == null) {
            agregarMensaje(FacesMessage.SEVERITY_WARN, "procedimiento.arbol.examen.seleccione");
            return;
        }
        if (examenesForm.contains(examenAgregar)) {
            agregarMensaje(FacesMessage.SEVERITY_WARN, "procedimiento.arbol.examen.yaAgregado");
        } else {
            examenesForm.add(examenAgregar);
        }
        examenAgregar = null;
    }

    /** Quita de la lista el examen seleccionado en la tabla. */
    public void quitarExamen() {
        if (examenSeleccionado == null) {
            agregarMensaje(FacesMessage.SEVERITY_WARN, "procedimiento.arbol.examen.seleccioneQuitar");
            return;
        }
        examenesForm.remove(examenSeleccionado);
        examenSeleccionado = null;
    }


    public void eliminarPaso(ProcedimientoPaso paso) {
        try {
            service.eliminarPaso(paso.getIdProcedimientoPaso());
            construirArbol();
        } catch (ReglaNegocioException ex) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), null));
        } catch (Exception ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
            agregarMensaje(FacesMessage.SEVERITY_ERROR, "procedimiento.arbol.error.eliminar");
        }
    }

    private void agregarMensaje(FacesMessage.Severity severidad, String clave) {
        FacesContext ctx = FacesContext.getCurrentInstance();
        String texto = ctx.getApplication().getResourceBundle(ctx, "msg").getString(clave);
        ctx.addMessage(null, new FacesMessage(severidad, texto, null));
    }

}
package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf;

import jakarta.faces.event.ActionEvent;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ParentServiceInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ExamenService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Examen;

@Named
@ViewScoped
public class ExamenTipoExamenModel extends ModelHandler<Examen> {

    private static final long serialVersionUID = 1L;

    @Inject
    private ExamenService examenService;

    @Inject
    private ExamenTipoExamenArbolModel arbolModel;

    private GenericLazyDataModel<Examen> lazyModel;

    public ExamenTipoExamenModel() {
        super(Examen.class);
        this.lazyModel = new GenericLazyDataModel<>(this);
    }

    public GenericLazyDataModel<Examen> getLazyModel() {
        return lazyModel;
    }

    @Override
    public ParentServiceInterface<Examen> getDAO() {
        return examenService;
    }

    @Override
    public Examen instanciarRegistro() {
        return new Examen();
    }

    @Override
    public Examen getRegistroById(String id) {
        try {
            UUID uuid = UUID.fromString(id);
            return (Examen) examenService.buscar(uuid);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(getClass().getName()).log(
                    Level.WARNING,
                    "ID inválido recibido para Examen: " + id,
                    ex);
            return null;
        }
    }

    @Override
    public Object getIdByRegistro(Examen registro) {
        return registro != null ? registro.getIdExamen() : null;
    }

    @Override
    public void nuevo() {
        super.nuevo();
        arbolModel.limpiar();
    }

    @Override
    public void seleccionar(org.primefaces.event.SelectEvent<Examen> evento) {
        super.seleccionar(evento);
        arbolModel.alEntrarPestana();
    }

    @Override
    public void guardarHandler(ActionEvent ae)
            throws IllegalArgumentException, IllegalStateException {

        try {
            super.guardarHandler(ae);

            if (getRegistroActual() != null
                    && getRegistroActual().getIdExamen() != null) {

                arbolModel.guardarTipos();
            }

        } catch (Exception ex) {
            Logger.getLogger(getClass().getName()).log(
                    Level.SEVERE,
                    ex.getMessage(),
                    ex);

            throw new IllegalStateException(ex);
        }
    }
}
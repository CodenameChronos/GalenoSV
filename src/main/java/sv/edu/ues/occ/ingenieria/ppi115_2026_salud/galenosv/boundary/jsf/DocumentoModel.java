package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ParentServiceInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.DocumentoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.PersonaService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.TipoDocumentoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Documento;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Persona;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.TipoDocumento;

/**
 *
 * @author kardia
 */
@Named
@ViewScoped
public class DocumentoModel extends ModelHandler<Documento> {

    private static final long serialVersionUID = 1L;

    @Inject
    private DocumentoService docService;

    @Inject
    private PersonaService persService;

    @Inject
    private TipoDocumentoService tdService;

    private GenericLazyDataModel<Documento> lazyModel;

    public DocumentoModel() {
        super(Documento.class);

        this.lazyModel = new GenericLazyDataModel<>(this);
    }

    @Override
    public ParentServiceInterface<Documento> getDAO() {
        return docService;
    }

    public GenericLazyDataModel<Documento> getLazyModel() {
        return lazyModel;
    }

    @Override
    public Documento instanciarRegistro() {
        return new Documento();
    }

    @Override
    public Documento getRegistroById(String id) {
        try {
            UUID uuid = UUID.fromString(id);
            return (Documento) docService.buscar(uuid);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(getClass().getName()).log(Level.WARNING,
                    "ID inválido recibido para Documento: " + id, ex);
            return null;
        }
    }

    @Override
    public Object getIdByRegistro(Documento registro) {
        return registro != null ? registro.getIdDocumento() : null;
    }

    public List<Persona> completarPersona(String texto) {
        if (texto == null || texto.isBlank()) {
            return List.of();
        }
        return persService.buscarPorNombre(texto, 30);
    }

    public List<TipoDocumento> completarTipoDocumento(String texto) {
        if (texto == null || texto.isBlank()) {
            return List.of();
        }
        return tdService.buscarPorNombre(texto, 30);
    }

    private Persona personaFiltro;

    public void filtrarPorPersona(Persona persona) {
        this.personaFiltro = persona;
        if (persona != null) {
            setFiltro("idPersona.idPersona", persona.getIdPersona());
        } else {
            limpiarFiltro();
        }
    }

    public Persona getPersonaFiltro() {
        return personaFiltro;
    }

    @Override
    public void nuevo() {
        super.nuevo();
        if (personaFiltro != null) {
            registroActual.setIdPersona(personaFiltro);
        }
    }

}

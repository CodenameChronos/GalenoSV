package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.PersonaRolDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ProcedimientoPasoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ReglaNegocioException;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Clinica;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ConsultaProcedimiento;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ConsultaProcedimientoPaso;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.PersonaRol;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ProcedimientoPaso;

/**
 * Orquesta la aplicación de un procedimiento a una consulta.
 *
 * <p>Un {@code Procedimiento} es una plantilla (una cadena de pasos). Al
 * aplicarlo a una consulta se crea un {@link ConsultaProcedimiento} y, junto
 * con él, <b>solo el paso inicial</b> de la plantilla, con un responsable
 * elegido al azar entre el personal de la clínica (todos menos pacientes). Los
 * pasos siguientes se crearán a medida que avance el procedimiento. Todo se
 * guarda en una sola transacción: si algo falla, no queda nada a medias.</p>
 *
 * <p>Las violaciones de regla se lanzan como {@link ReglaNegocioException}, para
 * que la pantalla muestre el mensaje tal cual en vez de uno genérico.</p>
 */
@Stateless
public class ConsultaProcedimientoService {

    @PersistenceContext
    private EntityManager em;

    @Inject
    private ProcedimientoPasoService procedimientoPasoService;

    @Inject
    private PersonaRolDAO personaRolDAO;

    /**
     * Crea el procedimiento de la consulta junto con su paso inicial, asignado
     * a una persona del personal de la clínica elegida al azar.
     *
     * @param nuevo el procedimiento de consulta a crear, con consulta,
     * procedimiento y fecha de inicio ya asignados.
     * @param clinica la clínica de la sesión, de donde sale el personal.
     * @throws ReglaNegocioException si no hay sesión de clínica, si la consulta
     * aún no está guardada, si no se eligió procedimiento, si el procedimiento
     * no tiene paso inicial, o si la clínica no tiene personal al que asignar.
     */
    public void crearConPasoInicial(ConsultaProcedimiento nuevo, Clinica clinica) {
        if (clinica == null) {
            throw new ReglaNegocioException("Debe iniciar sesión para agregar procedimientos.");
        }
        if (nuevo.getIdConsulta() == null || nuevo.getIdConsulta().getIdConsulta() == null) {
            throw new ReglaNegocioException("Guarde la consulta antes de agregarle procedimientos.");
        }
        if (nuevo.getIdProcedimiento() == null) {
            throw new ReglaNegocioException("Seleccione el procedimiento a aplicar.");
        }

        UUID idProcedimiento = nuevo.getIdProcedimiento().getIdProcedimiento();

        // Todas las validaciones van ANTES de persistir nada.
        ProcedimientoPaso pasoInicial = procedimientoPasoService.obtenerPasoInicial(idProcedimiento);
        if (pasoInicial == null) {
            throw new ReglaNegocioException(
                    "El procedimiento elegido todavía no tiene un paso inicial definido.");
        }

        List<PersonaRol> candidatos =
                personaRolDAO.listarPersonalExcluyendoRol(clinica, PersonaRolDAO.ROL_PACIENTE);
        if (candidatos.isEmpty()) {
            throw new ReglaNegocioException(
                    "No hay personal disponible en esta clínica para asignar el primer paso.");
        }
        PersonaRol responsable = candidatos.get(new Random().nextInt(candidatos.size()));

        em.persist(nuevo);

        ConsultaProcedimientoPaso primerPaso = new ConsultaProcedimientoPaso();
        primerPaso.setIdConsultaProcedimiento(nuevo);
        primerPaso.setIdProcedimientoPaso(pasoInicial);
        primerPaso.setIdPersonaRol(responsable);
        primerPaso.setFechaInicio(new Date());
        em.persist(primerPaso);
    }

}
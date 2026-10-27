package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.*;

/**
 *
 * @author kardia
 */

@Stateless
@LocalBean
public class ConsultaProcedimientoService extends ParentService<ConsultaProcedimiento> {

    @PersistenceContext(unitName = "Galeno-PU")
    EntityManager em;

    @Inject
    private ProcedimientoPasoService procedimientoPasoService;

    @Inject
    private PersonaRolService personaRolService;

    public ConsultaProcedimientoService() {
        super(ConsultaProcedimiento.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

   public List<ConsultaProcedimiento> buscarPorNombreProcedimiento(String texto, int max) {
       return getEntityManager()
               .createNamedQuery("ConsultaProcedimiento.findByNombreProcedimiento", ConsultaProcedimiento.class)
               .setParameter("texto", "%" + texto.trim().toLowerCase() + "%")
               .setMaxResults(max)
               .getResultList();
   }

    public List<ConsultaProcedimiento> listarPorConsulta(UUID consulta, int first, int max) {
        return getEntityManager().createNamedQuery(
                        "ConsultaProcedimiento.listarPorConsulta",
                        ConsultaProcedimiento.class)
                .setParameter("consulta", consulta)
                .setFirstResult(first)
                .setMaxResults(max)
                .getResultList();
    }

    public long contarPorConsulta(UUID consulta) {
        return getEntityManager().createNamedQuery(
                        "ConsultaProcedimiento.contarPorConsulta",
                        Long.class)
                .setParameter("consulta", consulta)
                .getSingleResult();
    }

    public List<Procedimiento> buscarProcedimientosActivos(String texto, int max) {
        return getEntityManager().createNamedQuery(
                "Procedimiento.findActiveByNombre",
                        Procedimiento.class)
                .setParameter("nombre", "%" + texto.trim().toLowerCase() + "%")
                .setMaxResults(max)
                .getResultList();
    }

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

        ProcedimientoPaso pasoInicial = procedimientoPasoService.obtenerPasoInicial(idProcedimiento);
        if (pasoInicial == null) {
            throw new ReglaNegocioException(
                    "El procedimiento elegido todavía no tiene un paso inicial definido.");
        }

        List<PersonaRol> candidatos =
                personaRolService.listarPersonalExcluyendoRol(clinica, PersonaRolService.ROL_PACIENTE);
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
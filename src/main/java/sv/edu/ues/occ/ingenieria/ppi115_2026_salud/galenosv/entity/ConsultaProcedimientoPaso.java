package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity;

import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "consulta_procedimiento_paso", schema = "public")
@NamedQueries({
    @NamedQuery(name = "ConsultaProcedimientoPaso.findAll", query = "SELECT c FROM ConsultaProcedimientoPaso c"),
    @NamedQuery(name = "ConsultaProcedimientoPaso.findByFechaInicio", query = "SELECT c FROM ConsultaProcedimientoPaso c WHERE c.fechaInicio = :fechaInicio"),
    @NamedQuery(name = "ConsultaProcedimientoPaso.findByFechaFin", query = "SELECT c FROM ConsultaProcedimientoPaso c WHERE c.fechaFin = :fechaFin"),
    @NamedQuery(name = "ConsultaProcedimientoPaso.findByEstado", query = "SELECT c FROM ConsultaProcedimientoPaso c WHERE c.estado = :estado"),
    @NamedQuery(name = "ConsultaProcedimientoPaso.listarPorConsultaProcedimiento", query = "SELECT cpp FROM ConsultaProcedimientoPaso cpp LEFT JOIN FETCH cpp.idProcedimientoPaso LEFT JOIN FETCH cpp.idPersonaRol pr LEFT JOIN FETCH pr.idPersona LEFT JOIN FETCH pr.idRol WHERE cpp.idConsultaProcedimiento.idConsultaProcedimiento = :idConsultaProcedimiento ORDER BY cpp.fechaInicio")})
public class ConsultaProcedimientoPaso implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Estado con el que nace todo paso. Los demás estados se definirán junto con el flujo de avance. */
    public static final String ESTADO_CREADO = "CREADO";

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_consulta_procedimiento_paso")
    private UUID idConsultaProcedimientoPaso;

    @NotNull(message = "La fecha de inicio del paso es obligatoria")
    @PastOrPresent(message = "La fecha de inicio no puede ser una fecha futura")
    @Column(name = "fecha_inicio", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaInicio;

    @PastOrPresent(message = "La fecha de fin no puede ser una fecha futura")
    @Column(name = "fecha_fin")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaFin;

    @NotBlank(message = "El estado del paso es obligatorio")
    @Size(max = 30, message = "El estado no debe exceder los 30 caracteres")
    @Column(name = "estado", nullable = false, length = 30)
    private String estado = ESTADO_CREADO;

    @NotNull(message = "El procedimiento asociado es obligatorio")
    @JoinColumn(name = "id_consulta_procedimiento", referencedColumnName = "id_consulta_procedimiento", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private ConsultaProcedimiento idConsultaProcedimiento;

    /** Paso de la plantilla (ProcedimientoPaso) del que nace este paso de la consulta. */
    @NotNull(message = "El paso del procedimiento es obligatorio")
    @JoinColumn(name = "id_procedimiento_paso", referencedColumnName = "id_procedimiento_paso", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private ProcedimientoPaso idProcedimientoPaso;

    @NotNull(message = "La persona con rol asignado es obligatoria")
    @JoinColumn(name = "id_persona_rol", referencedColumnName = "id_persona_rol", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private PersonaRol idPersonaRol;

    @OneToMany(mappedBy = "idConsultaProcedimientoPaso", fetch = FetchType.LAZY)
    private List<OrdenExamen> ordenExamenList;

    public ConsultaProcedimientoPaso() {
    }

    public ConsultaProcedimientoPaso(UUID idConsultaProcedimientoPaso) {
        this.idConsultaProcedimientoPaso = idConsultaProcedimientoPaso;
    }

    @JsonbTransient
    @AssertTrue(message = "La fecha de fin no puede ser anterior a la fecha de inicio")
    public boolean isRangoFechasValido() {
        return fechaInicio == null || fechaFin == null || !fechaFin.before(fechaInicio);
    }

    public UUID getIdConsultaProcedimientoPaso() {
        return idConsultaProcedimientoPaso;
    }

    public void setIdConsultaProcedimientoPaso(UUID idConsultaProcedimientoPaso) {
        this.idConsultaProcedimientoPaso = idConsultaProcedimientoPaso;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public ConsultaProcedimiento getIdConsultaProcedimiento() {
        return idConsultaProcedimiento;
    }

    public void setIdConsultaProcedimiento(ConsultaProcedimiento idConsultaProcedimiento) {
        this.idConsultaProcedimiento = idConsultaProcedimiento;
    }

    public ProcedimientoPaso getIdProcedimientoPaso() {
        return idProcedimientoPaso;
    }

    public void setIdProcedimientoPaso(ProcedimientoPaso idProcedimientoPaso) {
        this.idProcedimientoPaso = idProcedimientoPaso;
    }

    public PersonaRol getIdPersonaRol() {
        return idPersonaRol;
    }

    public void setIdPersonaRol(PersonaRol idPersonaRol) {
        this.idPersonaRol = idPersonaRol;
    }

    public List<OrdenExamen> getOrdenExamenList() {
        return ordenExamenList;
    }

    public void setOrdenExamenList(List<OrdenExamen> ordenExamenList) {
        this.ordenExamenList = ordenExamenList;
    }

    @Override
    public int hashCode() {
        return (idConsultaProcedimientoPaso != null) ? idConsultaProcedimientoPaso.hashCode() : super.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof ConsultaProcedimientoPaso)) {
            return false;
        }
        ConsultaProcedimientoPaso other = (ConsultaProcedimientoPaso) object;
        if (this.idConsultaProcedimientoPaso == null || other.idConsultaProcedimientoPaso == null) {
            return false;
        }
        return Objects.equals(this.idConsultaProcedimientoPaso, other.idConsultaProcedimientoPaso);
    }

    @Override
    public String toString() {
        return "sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ConsultaProcedimientoPaso[ idConsultaProcedimientoPaso=" + idConsultaProcedimientoPaso + " ]";
    }
}
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
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "consulta_procedimiento", schema = "public")
@NamedQueries({
    @NamedQuery(name = "ConsultaProcedimiento.findAll", query = "SELECT c FROM ConsultaProcedimiento c"),
    @NamedQuery(name = "ConsultaProcedimiento.findByFechaInicio", query = "SELECT c FROM ConsultaProcedimiento c WHERE c.fechaInicio = :fechaInicio"),
    @NamedQuery(name = "ConsultaProcedimiento.findByFechaFin", query = "SELECT c FROM ConsultaProcedimiento c WHERE c.fechaFin = :fechaFin"),
    @NamedQuery(name = "ConsultaProcedimiento.findByObservaciones", query = "SELECT c FROM ConsultaProcedimiento c WHERE c.observaciones = :observaciones"),
    @NamedQuery(name = "ConsultaProcedimiento.findByNombreProcedimiento", query = "SELECT consp FROM ConsultaProcedimiento consp LEFT JOIN FETCH consp.idProcedimiento LEFT JOIN FETCH consp.idConsulta c LEFT JOIN FETCH c.idPersonaRol pr LEFT JOIN FETCH pr.idPersona WHERE UPPER(consp.idProcedimiento.nombre) LIKE UPPER(:texto)")
})
public class ConsultaProcedimiento implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_consulta_procedimiento")
    private UUID idConsultaProcedimiento;

    @NotNull(message = "El identificador del procedimiento es obligatorio")
    @JoinColumn(name = "id_procedimiento", referencedColumnName = "id_procedimiento", nullable = false)
    private Procedimiento idProcedimiento;

    @NotNull(message = "La fecha de inicio del procedimiento es obligatoria")
    @PastOrPresent(message = "La fecha de inicio no puede ser una fecha futura")
    @Column(name = "fecha_inicio", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaInicio;

    @PastOrPresent(message = "La fecha de fin no puede ser una fecha futura")
    @Column(name = "fecha_fin")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaFin;

    @Size(max = 2000, message = "Las observaciones no deben exceder los 2000 caracteres")
    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @NotNull(message = "La consulta asociada es obligatoria")
    @JoinColumn(name = "id_consulta", referencedColumnName = "id_consulta", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Consulta idConsulta;

    @OneToMany(mappedBy = "idConsultaProcedimiento", fetch = FetchType.LAZY)
    private List<ConsultaProcedimientoPaso> consultaProcedimientoPasoList;

    public ConsultaProcedimiento() {
    }

    public ConsultaProcedimiento(UUID idConsultaProcedimiento) {
        this.idConsultaProcedimiento = idConsultaProcedimiento;
    }

    @JsonbTransient
    @AssertTrue(message = "La fecha de fin no puede ser anterior a la fecha de inicio")
    public boolean isRangoFechasValido() {
        return fechaInicio == null || fechaFin == null || !fechaFin.before(fechaInicio);
    }

    public UUID getIdConsultaProcedimiento() {
        return idConsultaProcedimiento;
    }

    public void setIdConsultaProcedimiento(UUID idConsultaProcedimiento) {
        this.idConsultaProcedimiento = idConsultaProcedimiento;
    }

    public Procedimiento getIdProcedimiento() {
        return idProcedimiento;
    }

    public void setIdProcedimiento(Procedimiento idProcedimiento) {
        this.idProcedimiento = idProcedimiento;
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

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = (observaciones != null && !observaciones.isBlank()) ? observaciones.trim() : null;
    }

    public List<ConsultaProcedimientoPaso> getConsultaProcedimientoPasoList() {
        return consultaProcedimientoPasoList;
    }

    public void setConsultaProcedimientoPasoList(List<ConsultaProcedimientoPaso> consultaProcedimientoPasoList) {
        this.consultaProcedimientoPasoList = consultaProcedimientoPasoList;
    }

    public Consulta getIdConsulta() {
        return idConsulta;
    }

    public void setIdConsulta(Consulta idConsulta) {
        this.idConsulta = idConsulta;
    }

    @Override
    public int hashCode() {
        return (idConsultaProcedimiento != null) ? idConsultaProcedimiento.hashCode() : super.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof ConsultaProcedimiento)) {
            return false;
        }
        ConsultaProcedimiento other = (ConsultaProcedimiento) object;
        if (this.idConsultaProcedimiento == null || other.idConsultaProcedimiento == null) {
            return false;
        }
        return Objects.equals(this.idConsultaProcedimiento, other.idConsultaProcedimiento);
    }

    @Override
    public String toString() {
        return "sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ConsultaProcedimiento[ idConsultaProcedimiento=" + idConsultaProcedimiento + " ]";
    }
}
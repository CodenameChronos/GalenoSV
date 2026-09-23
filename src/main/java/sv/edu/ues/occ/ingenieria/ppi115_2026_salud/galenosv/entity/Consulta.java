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
@Table(name = "consulta", schema = "public")
@NamedQueries({
    @NamedQuery(name = "Consulta.findAll", query = "SELECT c FROM Consulta c"),
    @NamedQuery(name = "Consulta.findByFechaInicio", query = "SELECT c FROM Consulta c WHERE c.fechaInicio = :fechaInicio"),
    @NamedQuery(name = "Consulta.findByFechaFin", query = "SELECT c FROM Consulta c WHERE c.fechaFin = :fechaFin"),
    @NamedQuery(name = "Consulta.findByReferenciaExterna", query = "SELECT c FROM Consulta c WHERE c.referenciaExterna = :referenciaExterna"),
    @NamedQuery(name = "Consulta.findByObservaciones", query = "SELECT c FROM Consulta c WHERE c.observaciones = :observaciones")})
public class Consulta implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_consulta")
    private UUID idConsulta;

    @NotNull(message = "La fecha de inicio de la consulta es obligatoria")
    @PastOrPresent(message = "La fecha de inicio no puede ser una fecha futura")
    @Column(name = "fecha_inicio", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaInicio;

    @PastOrPresent(message = "La fecha de fin no puede ser una fecha futura")
    @Column(name = "fecha_fin")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaFin;

    @Size(max = 255, message = "La referencia externa no debe exceder los 255 caracteres")
    @Column(name = "referencia_externa", length = 255)
    private String referenciaExterna;

    @Size(max = 2000, message = "Las observaciones no deben exceder los 2000 caracteres")
    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @NotNull(message = "La persona asociada a la consulta es obligatoria")
    @JoinColumn(name = "id_persona_rol", referencedColumnName = "id_persona_rol", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private PersonaRol idPersonaRol;

    @OneToMany(mappedBy = "idConsulta", fetch = FetchType.LAZY)
    private List<ConsultaProcedimiento> consultaProcedimientoList;

    public Consulta() {
    }

    public Consulta(UUID idConsulta) {
        this.idConsulta = idConsulta;
    }

    // --- Validación entre múltiples campos ---
    @JsonbTransient
    @AssertTrue(message = "La fecha de fin no puede ser anterior a la fecha de inicio")
    public boolean isRangoFechasValido() {
        return fechaInicio == null || fechaFin == null || !fechaFin.before(fechaInicio);
    }

    public UUID getIdConsulta() {
        return idConsulta;
    }

    public void setIdConsulta(UUID idConsulta) {
        this.idConsulta = idConsulta;
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

    public String getReferenciaExterna() {
        return referenciaExterna;
    }

    public void setReferenciaExterna(String referenciaExterna) {
        this.referenciaExterna = (referenciaExterna != null && !referenciaExterna.isBlank()) ? referenciaExterna.trim() : null;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = (observaciones != null && !observaciones.isBlank()) ? observaciones.trim() : null;
    }

    public PersonaRol getIdPersonaRol() {
        return idPersonaRol;
    }

    public void setIdPersonaRol(PersonaRol idPersonaRol) {
        this.idPersonaRol = idPersonaRol;
    }

    public List<ConsultaProcedimiento> getConsultaProcedimientoList() {
        return consultaProcedimientoList;
    }

    public void setConsultaProcedimientoList(List<ConsultaProcedimiento> consultaProcedimientoList) {
        this.consultaProcedimientoList = consultaProcedimientoList;
    }

    @Override
    public int hashCode() {
        return (idConsulta != null) ? idConsulta.hashCode() : super.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Consulta)) {
            return false;
        }
        Consulta other = (Consulta) object;
        if (this.idConsulta == null || other.idConsulta == null) {
            return false;
        }
        return Objects.equals(this.idConsulta, other.idConsulta);
    }

    @Override
    public String toString() {
        return "sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Consulta[ idConsulta=" + idConsulta + " ]";
    }
}
package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity;

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
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "persona_rol", schema = "public")
@NamedQueries({
    @NamedQuery(name = "PersonaRol.findAll", query = "SELECT p FROM PersonaRol p"),
    @NamedQuery(name = "PersonaRol.findByFechaCreacion", query = "SELECT p FROM PersonaRol p WHERE p.fechaCreacion = :fechaCreacion")})
public class PersonaRol implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_persona_rol")
    private UUID idPersonaRol;

    @NotNull(message = "La fecha de creación es obligatoria")
    @PastOrPresent(message = "La fecha de creación no puede ser una fecha futura")
    @Column(name = "fecha_creacion", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion = new Date();

    @OneToMany(mappedBy = "idPersonaRol", fetch = FetchType.LAZY)
    private List<ConsultaProcedimientoPaso> consultaProcedimientoPasoList;

    @NotNull(message = "La clínica es obligatoria")
    @JoinColumn(name = "id_clinica", referencedColumnName = "id_clinica", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Clinica idClinica;

    @NotNull(message = "La persona es obligatoria")
    @JoinColumn(name = "id_persona", referencedColumnName = "id_persona", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Persona idPersona;

    @NotNull(message = "El rol es obligatorio")
    @JoinColumn(name = "id_rol", referencedColumnName = "id_rol", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Rol idRol;

    @OneToMany(mappedBy = "idPersonaRol", fetch = FetchType.LAZY)
    private List<Consulta> consultaList;

    public PersonaRol() {
    }

    public PersonaRol(UUID idPersonaRol) {
        this.idPersonaRol = idPersonaRol;
    }

    public UUID getIdPersonaRol() {
        return idPersonaRol;
    }

    public void setIdPersonaRol(UUID idPersonaRol) {
        this.idPersonaRol = idPersonaRol;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public List<ConsultaProcedimientoPaso> getConsultaProcedimientoPasoList() {
        return consultaProcedimientoPasoList;
    }

    public void setConsultaProcedimientoPasoList(List<ConsultaProcedimientoPaso> consultaProcedimientoPasoList) {
        this.consultaProcedimientoPasoList = consultaProcedimientoPasoList;
    }

    public Clinica getIdClinica() {
        return idClinica;
    }

    public void setIdClinica(Clinica idClinica) {
        this.idClinica = idClinica;
    }

    public Persona getIdPersona() {
        return idPersona;
    }

    public void setIdPersona(Persona idPersona) {
        this.idPersona = idPersona;
    }

    public Rol getIdRol() {
        return idRol;
    }

    public void setIdRol(Rol idRol) {
        this.idRol = idRol;
    }

    public List<Consulta> getConsultaList() {
        return consultaList;
    }

    public void setConsultaList(List<Consulta> consultaList) {
        this.consultaList = consultaList;
    }

    @Override
    public int hashCode() {
        return (idPersonaRol != null) ? idPersonaRol.hashCode() : super.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof PersonaRol)) {
            return false;
        }
        PersonaRol other = (PersonaRol) object;
        if (this.idPersonaRol == null || other.idPersonaRol == null) {
            return false;
        }
        return Objects.equals(this.idPersonaRol, other.idPersonaRol);
    }

    @Override
    public String toString() {
        return "sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.PersonaRol[ idPersonaRol=" + idPersonaRol + " ]";
    }
}

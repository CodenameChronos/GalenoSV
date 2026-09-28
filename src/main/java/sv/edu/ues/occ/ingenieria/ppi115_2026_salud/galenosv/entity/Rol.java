package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "rol", schema = "public")
@NamedQueries({
    @NamedQuery(name = "Rol.findAll", query = "SELECT r FROM Rol r"),
    @NamedQuery(name = "Rol.findByNombre", query = "SELECT r FROM Rol r WHERE r.nombre = :nombre"),
    @NamedQuery(name = "Rol.findByActivo", query = "SELECT r FROM Rol r WHERE r.activo = :activo"),
    @NamedQuery(name = "Rol.findByObservaciones", query = "SELECT r FROM Rol r WHERE r.observaciones = :observaciones"),
    @NamedQuery(
            name = "Rol.findActiveByNombre",
            query = "SELECT r FROM Rol r WHERE UPPER(r.nombre) LIKE UPPER(:nombre) AND r.activo = true ORDER BY r.nombre"
    )})
public class Rol implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_rol")
    private UUID idRol;

    @NotBlank(message = "El nombre del rol es obligatorio")
    @Size(max = 155, message = "El nombre no debe exceder los 155 caracteres")
    @Column(name = "nombre", nullable = false, length = 155)
    private String nombre;

    @NotNull(message = "El estado activo es obligatorio")
    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @Size(max = 2000, message = "Las observaciones no deben exceder los 2000 caracteres")
    @Column(name = "observaciones", length = 2000)
    private String observaciones;

    @OneToMany(mappedBy = "idRol", fetch = FetchType.LAZY)
    private List<PersonaRol> personaRolList;

    @OneToMany(mappedBy = "idRol", fetch = FetchType.LAZY)
    private List<ProcedimientoPaso> procedimientoPasoList;

    public Rol() {
    }

    public Rol(UUID idRol) {
        this.idRol = idRol;
    }

    public UUID getIdRol() {
        return idRol;
    }

    public void setIdRol(UUID idRol) {
        this.idRol = idRol;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = (nombre != null && !nombre.isBlank()) ? nombre.trim() : null;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = (observaciones != null && !observaciones.isBlank()) ? observaciones.trim() : null;
    }

    public List<PersonaRol> getPersonaRolList() {
        return personaRolList;
    }

    public void setPersonaRolList(List<PersonaRol> personaRolList) {
        this.personaRolList = personaRolList;
    }

    public List<ProcedimientoPaso> getProcedimientoPasoList() {
        return procedimientoPasoList;
    }

    public void setProcedimientoPasoList(List<ProcedimientoPaso> procedimientoPasoList) {
        this.procedimientoPasoList = procedimientoPasoList;
    }

    @Override
    public int hashCode() {
        return (idRol != null) ? idRol.hashCode() : super.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Rol)) {
            return false;
        }
        Rol other = (Rol) object;
        if (this.idRol == null || other.idRol == null) {
            return false;
        }
        return Objects.equals(this.idRol, other.idRol);
    }

    @Override
    public String toString() {
        return "sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Rol[ idRol=" + idRol + " ]";
    }
}

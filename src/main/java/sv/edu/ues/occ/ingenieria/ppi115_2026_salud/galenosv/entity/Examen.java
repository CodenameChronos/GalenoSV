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
@Table(name = "examen", schema = "public")
@NamedQueries({
    @NamedQuery(name = "Examen.findAll", query = "SELECT e FROM Examen e"),
    @NamedQuery(name = "Examen.findByNombre", query = "SELECT e FROM Examen e WHERE e.nombre = :nombre"),
    @NamedQuery(name = "Examen.findByActivo", query = "SELECT e FROM Examen e WHERE e.activo = :activo"),
    @NamedQuery(name = "Examen.findByObservaciones", query = "SELECT e FROM Examen e WHERE e.observaciones = :observaciones"),
    @NamedQuery(name = "Examen.findActiveByNombre", query = "SELECT e FROM Examen e WHERE UPPER(e.nombre) LIKE UPPER(:nombre) AND e.activo = true ORDER BY e.nombre")
})
public class Examen implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_examen")
    private UUID idExamen;

    @NotBlank(message = "El nombre del examen es obligatorio")
    @Size(max = 255, message = "El nombre del examen no debe exceder los 255 caracteres")
    @Column(name = "nombre", nullable = false, length = 255)
    private String nombre;

    @NotNull(message = "El estado activo/inactivo es obligatorio")
    @Column(name = "activo", nullable = false)
    private Boolean activo = Boolean.TRUE;

    @Size(max = 1000, message = "Las observaciones no deben exceder los 1000 caracteres")
    @Column(name = "observaciones", length = 1000)
    private String observaciones;

    @OneToMany(mappedBy = "idExamen", fetch = FetchType.LAZY)
    private List<ExamenTipoExamen> examenTipoExamenList;

    @OneToMany(mappedBy = "idExamen", fetch = FetchType.LAZY)
    private List<ProcedimientoPasoExamen> procedimientoPasoExamenList;

    public Examen() {
    }

    public Examen(UUID idExamen) {
        this.idExamen = idExamen;
    }

    public UUID getIdExamen() {
        return idExamen;
    }

    public void setIdExamen(UUID idExamen) {
        this.idExamen = idExamen;
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

    public List<ExamenTipoExamen> getExamenTipoExamenList() {
        return examenTipoExamenList;
    }

    public void setExamenTipoExamenList(List<ExamenTipoExamen> examenTipoExamenList) {
        this.examenTipoExamenList = examenTipoExamenList;
    }

    public List<ProcedimientoPasoExamen> getProcedimientoPasoExamenList() {
        return procedimientoPasoExamenList;
    }

    public void setProcedimientoPasoExamenList(List<ProcedimientoPasoExamen> procedimientoPasoExamenList) {
        this.procedimientoPasoExamenList = procedimientoPasoExamenList;
    }

    @Override
    public int hashCode() {
        return (idExamen != null) ? idExamen.hashCode() : super.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Examen)) {
            return false;
        }
        Examen other = (Examen) object;
        if (this.idExamen == null || other.idExamen == null) {
            return false;
        }
        return Objects.equals(this.idExamen, other.idExamen);
    }

    @Override
    public String toString() {
        return "sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Examen[ idExamen=" + idExamen + " ]";
    }
}
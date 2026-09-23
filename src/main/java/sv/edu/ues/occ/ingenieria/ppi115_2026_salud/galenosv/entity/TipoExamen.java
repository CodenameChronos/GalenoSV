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
@Table(name = "tipo_examen", schema = "public")
@NamedQueries({
    @NamedQuery(name = "TipoExamen.findAll", query = "SELECT t FROM TipoExamen t"),
    @NamedQuery(name = "TipoExamen.findByNombre", query = "SELECT t FROM TipoExamen t WHERE t.nombre = :nombre"),
    @NamedQuery(name = "TipoExamen.findByActivo", query = "SELECT t FROM TipoExamen t WHERE t.activo = :activo"),
    @NamedQuery(name = "TipoExamen.findByObservaciones", query = "SELECT t FROM TipoExamen t WHERE t.observaciones = :observaciones")})
public class TipoExamen implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_tipo_examen")
    private UUID idTipoExamen;

    @NotBlank(message = "El nombre del tipo de examen es obligatorio")
    @Size(min = 3, max = 155, message = "El nombre debe tener entre 3 y 155 caracteres")
    @Column(name = "nombre", nullable = false, length = 155)
    private String nombre;

    @NotNull(message = "El estado activo es obligatorio")
    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @Size(max = 2000, message = "Las observaciones no deben exceder los 2000 caracteres")
    @Column(name = "observaciones", length = 2000)
    private String observaciones;

    @OneToMany(mappedBy = "idTipoExamen", fetch = FetchType.LAZY)
    private List<ExamenTipoExamen> examenTipoExamenList;

    public TipoExamen() {
    }

    public TipoExamen(UUID idTipoExamen) {
        this.idTipoExamen = idTipoExamen;
    }

    public UUID getIdTipoExamen() {
        return idTipoExamen;
    }

    public void setIdTipoExamen(UUID idTipoExamen) {
        this.idTipoExamen = idTipoExamen;
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

    @Override
    public int hashCode() {
        return (idTipoExamen != null) ? idTipoExamen.hashCode() : super.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof TipoExamen)) {
            return false;
        }
        TipoExamen other = (TipoExamen) object;
        if (this.idTipoExamen == null || other.idTipoExamen == null) {
            return false;
        }
        return Objects.equals(this.idTipoExamen, other.idTipoExamen);
    }

    @Override
    public String toString() {
        return "sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.TipoExamen[ idTipoExamen=" + idTipoExamen + " ]";
    }
}
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
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "examen_tipo_examen", schema = "public")
@NamedQueries({
    @NamedQuery(name = "ExamenTipoExamen.findAll", query = "SELECT e FROM ExamenTipoExamen e"),
    @NamedQuery(name = "ExamenTipoExamen.findByFechaCreacion", query = "SELECT e FROM ExamenTipoExamen e WHERE e.fechaCreacion = :fechaCreacion"),
    @NamedQuery(name = "ExamenTipoExamen.findByObservaciones", query = "SELECT e FROM ExamenTipoExamen e WHERE e.observaciones = :observaciones"),
    @NamedQuery(name = "ExamenTipoExamen.findByIdExamen", query = "SELECT e FROM ExamenTipoExamen e WHERE e.idExamen.idExamen = :idExamen"),
    @NamedQuery(name = "ExamenTipoExamen.countByIdTipoExamen", query = "SELECT COUNT(e) FROM ExamenTipoExamen e WHERE e.idTipoExamen.idTipoExamen = :idTipoExamen")})
public class ExamenTipoExamen implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_examen_tipo_examen")
    private UUID idExamenTipoExamen;

    @NotNull(message = "La fecha de creación es obligatoria")
    @PastOrPresent(message = "La fecha de creación no puede ser una fecha futura")
    @Column(name = "fecha_creacion", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion = new Date();

    @Size(max = 1000, message = "Las observaciones no deben exceder los 1000 caracteres")
    @Column(name = "observaciones", length = 1000)
    private String observaciones;

    @NotNull(message = "El examen es obligatorio")
    @JoinColumn(name = "id_examen", referencedColumnName = "id_examen", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Examen idExamen;

    @NotNull(message = "El tipo de examen es obligatorio")
    @JoinColumn(name = "id_tipo_examen", referencedColumnName = "id_tipo_examen", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private TipoExamen idTipoExamen;

    public ExamenTipoExamen() {
    }

    public ExamenTipoExamen(UUID idExamenTipoExamen) {
        this.idExamenTipoExamen = idExamenTipoExamen;
    }

    public UUID getIdExamenTipoExamen() {
        return idExamenTipoExamen;
    }

    public void setIdExamenTipoExamen(UUID idExamenTipoExamen) {
        this.idExamenTipoExamen = idExamenTipoExamen;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = (observaciones != null && !observaciones.isBlank()) ? observaciones.trim() : null;
    }

    public Examen getIdExamen() {
        return idExamen;
    }

    public void setIdExamen(Examen idExamen) {
        this.idExamen = idExamen;
    }

    public TipoExamen getIdTipoExamen() {
        return idTipoExamen;
    }

    public void setIdTipoExamen(TipoExamen idTipoExamen) {
        this.idTipoExamen = idTipoExamen;
    }

    @Override
    public int hashCode() {
        return (idExamenTipoExamen != null) ? idExamenTipoExamen.hashCode() : super.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof ExamenTipoExamen)) {
            return false;
        }
        ExamenTipoExamen other = (ExamenTipoExamen) object;
        if (this.idExamenTipoExamen == null || other.idExamenTipoExamen == null) {
            return false;
        }
        return Objects.equals(this.idExamenTipoExamen, other.idExamenTipoExamen);
    }

    @Override
    public String toString() {
        return "sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ExamenTipoExamen[ idExamenTipoExamen=" + idExamenTipoExamen + " ]";
    }
}
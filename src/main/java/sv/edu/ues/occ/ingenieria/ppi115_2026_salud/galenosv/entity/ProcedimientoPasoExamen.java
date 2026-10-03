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
@Table(name = "procedimiento_paso_examen", schema = "public")
@NamedQueries({
        @NamedQuery(name = "ProcedimientoPasoExamen.findAll", query = "SELECT p FROM ProcedimientoPasoExamen p"),
        @NamedQuery(name = "ProcedimientoPasoExamen.findByFechaCreacion", query = "SELECT p FROM ProcedimientoPasoExamen p WHERE p.fechaCreacion = :fechaCreacion"),
        @NamedQuery(name = "ProcedimientoPasoExamen.findByActivo", query = "SELECT p FROM ProcedimientoPasoExamen p WHERE p.activo = :activo"),
        @NamedQuery(name = "ProcedimientoPasoExamen.findByObservaciones", query = "SELECT p FROM ProcedimientoPasoExamen p WHERE p.observaciones = :observaciones"),
        @NamedQuery(name = "ProcedimientoPasoExamen.findExamenByProcedimientoPaso", query = "SELECT e FROM ProcedimientoPasoExamen pe JOIN pe.idExamen e WHERE pe.idProcedimientoPaso.idProcedimientoPaso = :idPaso ORDER BY e.nombre")
})
public class ProcedimientoPasoExamen implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_procedimiento_paso_examen")
    private UUID idProcedimientoPasoExamen;

    @NotNull(message = "La fecha de creación es obligatoria")
    @PastOrPresent(message = "La fecha de creación no puede ser una fecha futura")
    @Column(name = "fecha_creacion", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion = new Date();

    @NotNull(message = "El estado activo es obligatorio")
    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @Size(max = 2000, message = "Las observaciones no deben exceder los 2000 caracteres")
    @Column(name = "observaciones", length = 2000)
    private String observaciones;

    @NotNull(message = "El examen es obligatorio")
    @JoinColumn(name = "id_examen", referencedColumnName = "id_examen", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Examen idExamen;

    @NotNull(message = "El paso de procedimiento es obligatorio")
    @JoinColumn(name = "id_procedimiento_paso", referencedColumnName = "id_procedimiento_paso", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private ProcedimientoPaso idProcedimientoPaso;

    public ProcedimientoPasoExamen() {
    }

    public ProcedimientoPasoExamen(UUID idProcedimientoPasoExamen) {
        this.idProcedimientoPasoExamen = idProcedimientoPasoExamen;
    }

    public UUID getIdProcedimientoPasoExamen() {
        return idProcedimientoPasoExamen;
    }

    public void setIdProcedimientoPasoExamen(UUID idProcedimientoPasoExamen) {
        this.idProcedimientoPasoExamen = idProcedimientoPasoExamen;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
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

    public Examen getIdExamen() {
        return idExamen;
    }

    public void setIdExamen(Examen idExamen) {
        this.idExamen = idExamen;
    }

    public ProcedimientoPaso getIdProcedimientoPaso() {
        return idProcedimientoPaso;
    }

    public void setIdProcedimientoPaso(ProcedimientoPaso idProcedimientoPaso) {
        this.idProcedimientoPaso = idProcedimientoPaso;
    }

    @Override
    public int hashCode() {
        return (idProcedimientoPasoExamen != null) ? idProcedimientoPasoExamen.hashCode() : super.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof ProcedimientoPasoExamen)) {
            return false;
        }
        ProcedimientoPasoExamen other = (ProcedimientoPasoExamen) object;
        if (this.idProcedimientoPasoExamen == null || other.idProcedimientoPasoExamen == null) {
            return false;
        }
        return Objects.equals(this.idProcedimientoPasoExamen, other.idProcedimientoPasoExamen);
    }

    @Override
    public String toString() {
        return "sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ProcedimientoPasoExamen[ idProcedimientoPasoExamen=" + idProcedimientoPasoExamen + " ]";
    }
}
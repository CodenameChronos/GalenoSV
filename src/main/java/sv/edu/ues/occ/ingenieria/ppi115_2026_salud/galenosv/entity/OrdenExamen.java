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
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "orden_examen", schema = "public")
@NamedQueries({
    @NamedQuery(name = "OrdenExamen.findAll", query = "SELECT o FROM OrdenExamen o"),
    @NamedQuery(name = "OrdenExamen.findByFechaCreacion", query = "SELECT o FROM OrdenExamen o WHERE o.fechaCreacion = :fechaCreacion"),
    @NamedQuery(name = "OrdenExamen.findByIndicaciones", query = "SELECT o FROM OrdenExamen o WHERE o.indicaciones = :indicaciones")})
public class OrdenExamen implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_orden_examen")
    private UUID idOrdenExamen;

    @NotNull(message = "La fecha de creación es obligatoria")
    @PastOrPresent(message = "La fecha de creación no puede ser una fecha futura")
    @Column(name = "fecha_creacion", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion = new Date();

    @Size(max = 2000, message = "Las indicaciones no deben exceder los 2000 caracteres")
    @Column(name = "indicaciones", length = 2000)
    private String indicaciones;

    @OneToMany(mappedBy = "idOrdenExamen", fetch = FetchType.LAZY)
    private List<ExamenResultado> examenResultadoList;

    @NotNull(message = "El paso del procedimiento en consulta es obligatorio")
    @JoinColumn(name = "id_consulta_procedimiento_paso", referencedColumnName = "id_consulta_procedimiento_paso", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private ConsultaProcedimientoPaso idConsultaProcedimientoPaso;

    public OrdenExamen() {
    }

    public OrdenExamen(UUID idOrdenExamen) {
        this.idOrdenExamen = idOrdenExamen;
    }

    public UUID getIdOrdenExamen() {
        return idOrdenExamen;
    }

    public void setIdOrdenExamen(UUID idOrdenExamen) {
        this.idOrdenExamen = idOrdenExamen;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getIndicaciones() {
        return indicaciones;
    }

    public void setIndicaciones(String indicaciones) {
        this.indicaciones = (indicaciones != null && !indicaciones.isBlank()) ? indicaciones.trim() : null;
    }

    public List<ExamenResultado> getExamenResultadoList() {
        return examenResultadoList;
    }

    public void setExamenResultadoList(List<ExamenResultado> examenResultadoList) {
        this.examenResultadoList = examenResultadoList;
    }

    public ConsultaProcedimientoPaso getIdConsultaProcedimientoPaso() {
        return idConsultaProcedimientoPaso;
    }

    public void setIdConsultaProcedimientoPaso(ConsultaProcedimientoPaso idConsultaProcedimientoPaso) {
        this.idConsultaProcedimientoPaso = idConsultaProcedimientoPaso;
    }

    @Override
    public int hashCode() {
        return (idOrdenExamen != null) ? idOrdenExamen.hashCode() : super.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof OrdenExamen)) {
            return false;
        }
        OrdenExamen other = (OrdenExamen) object;
        if (this.idOrdenExamen == null || other.idOrdenExamen == null) {
            return false;
        }
        return Objects.equals(this.idOrdenExamen, other.idOrdenExamen);
    }

    @Override
    public String toString() {
        return "sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.OrdenExamen[ idOrdenExamen=" + idOrdenExamen + " ]";
    }
}
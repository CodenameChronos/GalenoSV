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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "examen_resultado", schema = "public")
@NamedQueries({
    @NamedQuery(name = "ExamenResultado.findAll", query = "SELECT e FROM ExamenResultado e"),
    @NamedQuery(name = "ExamenResultado.findByFechaCreacion", query = "SELECT e FROM ExamenResultado e WHERE e.fechaCreacion = :fechaCreacion"),
    @NamedQuery(name = "ExamenResultado.findByResultado", query = "SELECT e FROM ExamenResultado e WHERE e.resultado = :resultado"),
    @NamedQuery(name = "ExamenResultado.findByInterpretacion", query = "SELECT e FROM ExamenResultado e WHERE e.interpretacion = :interpretacion"),
    @NamedQuery(name = "ExamenResultado.findByRutaAtestado", query = "SELECT e FROM ExamenResultado e WHERE e.rutaAtestado = :rutaAtestado")})
public class ExamenResultado implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_examen_resultado")
    private UUID idExamenResultado;

    @NotNull(message = "La fecha de creación es obligatoria")
    @PastOrPresent(message = "La fecha de creación no puede ser una fecha futura")
    @Column(name = "fecha_creacion", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion = new Date();

    @NotBlank(message = "El resultado del examen es obligatorio")
    @Size(max = 2000, message = "El resultado no debe exceder los 2000 caracteres")
    @Column(name = "resultado", nullable = false, length = 2000)
    private String resultado;

    @Size(max = 2000, message = "La interpretación no debe exceder los 2000 caracteres")
    @Column(name = "interpretacion", length = 2000)
    private String interpretacion;

    @Size(max = 500, message = "La ruta del atestado no debe exceder los 500 caracteres")
    @Column(name = "ruta_atestado", length = 500)
    private String rutaAtestado;

    @NotNull(message = "La orden de examen asociada es obligatoria")
    @JoinColumn(name = "id_orden_examen", referencedColumnName = "id_orden_examen", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private OrdenExamen idOrdenExamen;

    public ExamenResultado() {
    }

    public ExamenResultado(UUID idExamenResultado) {
        this.idExamenResultado = idExamenResultado;
    }

    public UUID getIdExamenResultado() {
        return idExamenResultado;
    }

    public void setIdExamenResultado(UUID idExamenResultado) {
        this.idExamenResultado = idExamenResultado;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = (resultado != null && !resultado.isBlank()) ? resultado.trim() : null;
    }

    public String getInterpretacion() {
        return interpretacion;
    }

    public void setInterpretacion(String interpretacion) {
        this.interpretacion = (interpretacion != null && !interpretacion.isBlank()) ? interpretacion.trim() : null;
    }

    public String getRutaAtestado() {
        return rutaAtestado;
    }

    public void setRutaAtestado(String rutaAtestado) {
        this.rutaAtestado = (rutaAtestado != null && !rutaAtestado.isBlank()) ? rutaAtestado.trim() : null;
    }

    public OrdenExamen getIdOrdenExamen() {
        return idOrdenExamen;
    }

    public void setIdOrdenExamen(OrdenExamen idOrdenExamen) {
        this.idOrdenExamen = idOrdenExamen;
    }

    @Override
    public int hashCode() {
        return (idExamenResultado != null) ? idExamenResultado.hashCode() : super.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof ExamenResultado)) {
            return false;
        }
        ExamenResultado other = (ExamenResultado) object;
        if (this.idExamenResultado == null || other.idExamenResultado == null) {
            return false;
        }
        return Objects.equals(this.idExamenResultado, other.idExamenResultado);
    }

    @Override
    public String toString() {
        return "sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ExamenResultado[ idExamenResultado=" + idExamenResultado + " ]";
    }
}
package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;


@Entity
@Table(name = "examen_resultado", catalog = "clinica", schema = "public")
@NamedQueries({
    @NamedQuery(name = "ExamenResultado.findAll", query = "SELECT e FROM ExamenResultado e"),
    @NamedQuery(name = "ExamenResultado.findByFechaCreacion", query = "SELECT e FROM ExamenResultado e WHERE e.fechaCreacion = :fechaCreacion"),
    @NamedQuery(name = "ExamenResultado.findByResultado", query = "SELECT e FROM ExamenResultado e WHERE e.resultado = :resultado"),
    @NamedQuery(name = "ExamenResultado.findByInterpretacion", query = "SELECT e FROM ExamenResultado e WHERE e.interpretacion = :interpretacion"),
    @NamedQuery(name = "ExamenResultado.findByRutaAtestado", query = "SELECT e FROM ExamenResultado e WHERE e.rutaAtestado = :rutaAtestado")})
public class ExamenResultado implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Lob
    @Column(name = "id_examen_resultado")
    @Convert(converter = UUIDConverter.class)
    private UUID idExamenResultado;
    @Column(name = "fecha_creacion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion;
    @Size(max = 2147483647)
    @Column(name = "resultado")
    private String resultado;
    @Size(max = 2147483647)
    @Column(name = "interpretacion")
    private String interpretacion;
    @Size(max = 2147483647)
    @Column(name = "ruta_atestado")
    private String rutaAtestado;
    @JoinColumn(name = "id_orden_examen", referencedColumnName = "id_orden_examen")
    @ManyToOne(fetch = FetchType.LAZY)
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
        this.resultado = resultado;
    }

    public String getInterpretacion() {
        return interpretacion;
    }

    public void setInterpretacion(String interpretacion) {
        this.interpretacion = interpretacion;
    }

    public String getRutaAtestado() {
        return rutaAtestado;
    }

    public void setRutaAtestado(String rutaAtestado) {
        this.rutaAtestado = rutaAtestado;
    }

    public OrdenExamen getIdOrdenExamen() {
        return idOrdenExamen;
    }

    public void setIdOrdenExamen(OrdenExamen idOrdenExamen) {
        this.idOrdenExamen = idOrdenExamen;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idExamenResultado != null ? idExamenResultado.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ExamenResultado)) {
            return false;
        }
        ExamenResultado other = (ExamenResultado) object;
        if ((this.idExamenResultado == null && other.idExamenResultado != null) || (this.idExamenResultado != null && !this.idExamenResultado.equals(other.idExamenResultado))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "sv.edu.ues.occ.ingenieria.pp115_2026_salud.galenosv.resources.ExamenResultado[ idExamenResultado=" + idExamenResultado + " ]";
    }
    
}

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
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "procedimiento_paso_secuencia", schema = "public")
@NamedQueries({
        @NamedQuery(name = "ProcedimientoPasoSecuencia.findAll", query = "SELECT p FROM ProcedimientoPasoSecuencia p"),
        @NamedQuery(name = "ProcedimientoPasoSecuencia.findByTipoSecuencia", query = "SELECT p FROM ProcedimientoPasoSecuencia p WHERE p.tipoSecuencia = :tipoSecuencia"),
        @NamedQuery(name = "ProcedimientoPasoSecuencia.findByNombreProcedimientoPaso", query = "SELECT pps FROM ProcedimientoPasoSecuencia pps LEFT JOIN FETCH pps.idProcedimientoPaso pp WHERE UPPER(pp.nombre) LIKE UPPER(:texto) ORDER BY pps.idProcedimientoPaso DESC"),
        @NamedQuery(name = "ProcedimientoPasoSecuencia.findByProcedimiento", query = "SELECT pps FROM ProcedimientoPasoSecuencia pps WHERE pps.idProcedimientoPaso.idProcedimiento.idProcedimiento = :idProcedimiento")
})
public class ProcedimientoPasoSecuencia implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_procedimiento_paso_secuencia")
    private UUID idProcedimientoPasoSecuencia;

    @NotNull(message = "El ID del paso de referencia es obligatorio")
    @Column(name = "id_procedimiento_paso_referencia", nullable = false)
    private UUID idProcedimientoPasoReferencia;

    @NotBlank(message = "El tipo de secuencia es obligatorio")
    @Size(max = 20, message = "El tipo de secuencia no debe exceder los 20 caracteres")
    @Column(name = "tipo_secuencia", nullable = false, length = 20)
    private String tipoSecuencia;

    @NotNull(message = "El paso de procedimiento asociado es obligatorio")
    @JoinColumn(name = "id_procedimiento_paso", referencedColumnName = "id_procedimiento_paso", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private ProcedimientoPaso idProcedimientoPaso;

    public ProcedimientoPasoSecuencia() {
    }

    public ProcedimientoPasoSecuencia(UUID idProcedimientoPasoSecuencia) {
        this.idProcedimientoPasoSecuencia = idProcedimientoPasoSecuencia;
    }

    @AssertTrue(message = "Un paso de procedimiento no puede ser su propia referencia")
    public boolean isReferenciaValida() {
        if (idProcedimientoPaso == null || idProcedimientoPaso.getIdProcedimientoPaso() == null || idProcedimientoPasoReferencia == null) {
            return true;
        }
        return !idProcedimientoPasoReferencia.equals(idProcedimientoPaso.getIdProcedimientoPaso());
    }

    public UUID getIdProcedimientoPasoSecuencia() {
        return idProcedimientoPasoSecuencia;
    }

    public void setIdProcedimientoPasoSecuencia(UUID idProcedimientoPasoSecuencia) {
        this.idProcedimientoPasoSecuencia = idProcedimientoPasoSecuencia;
    }

    public UUID getIdProcedimientoPasoReferencia() {
        return idProcedimientoPasoReferencia;
    }

    public void setIdProcedimientoPasoReferencia(UUID idProcedimientoPasoReferencia) {
        this.idProcedimientoPasoReferencia = idProcedimientoPasoReferencia;
    }

    public String getTipoSecuencia() {
        return tipoSecuencia;
    }

    public void setTipoSecuencia(String tipoSecuencia) {
        this.tipoSecuencia = (tipoSecuencia != null && !tipoSecuencia.isBlank()) ? tipoSecuencia.trim() : null;
    }

    public ProcedimientoPaso getIdProcedimientoPaso() {
        return idProcedimientoPaso;
    }

    public void setIdProcedimientoPaso(ProcedimientoPaso idProcedimientoPaso) {
        this.idProcedimientoPaso = idProcedimientoPaso;
    }

    @Override
    public int hashCode() {
        return (idProcedimientoPasoSecuencia != null) ? idProcedimientoPasoSecuencia.hashCode() : super.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof ProcedimientoPasoSecuencia)) {
            return false;
        }
        ProcedimientoPasoSecuencia other = (ProcedimientoPasoSecuencia) object;
        if (this.idProcedimientoPasoSecuencia == null || other.idProcedimientoPasoSecuencia == null) {
            return false;
        }
        return Objects.equals(this.idProcedimientoPasoSecuencia, other.idProcedimientoPasoSecuencia);
    }

    @Override
    public String toString() {
        return "idProcedimientoPasoSecuencia=" + idProcedimientoPasoSecuencia;
    }
}

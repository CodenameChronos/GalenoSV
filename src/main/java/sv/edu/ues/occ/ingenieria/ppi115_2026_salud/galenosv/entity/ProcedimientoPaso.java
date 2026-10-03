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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "procedimiento_paso", schema = "public")
@NamedQueries({
        @NamedQuery(name = "ProcedimientoPaso.findAll", query = "SELECT p FROM ProcedimientoPaso p"),
        @NamedQuery(name = "ProcedimientoPaso.findByNombre", query = "SELECT p FROM ProcedimientoPaso p WHERE p.nombre = :nombre"),
        @NamedQuery(name = "ProcedimientoPaso.findByIndicaFin", query = "SELECT p FROM ProcedimientoPaso p WHERE p.indicaFin = :indicaFin"),
        @NamedQuery(name = "ProcedimientoPaso.findActiveByNombre", query = "SELECT p FROM ProcedimientoPaso p WHERE UPPER(p.nombre) LIKE UPPER(:nombre) ORDER BY p.nombre"),
        @NamedQuery(name = "ProcedimientoPaso.findByProcedimiento", query = "SELECT p FROM ProcedimientoPaso p LEFT JOIN FETCH p.idRol WHERE p.idProcedimiento.idProcedimiento = :idProcedimiento"),
        @NamedQuery(name = "ProcedimientoPaso.countPasoSinPadre", query = "SELECT COUNT(p) FROM ProcedimientoPaso p WHERE p.idProcedimiento.idProcedimiento = :idProcedimiento AND p NOT IN (SELECT pps.idProcedimientoPaso FROM ProcedimientoPasoSecuencia pps WHERE pps.idProcedimientoPaso.idProcedimiento.idProcedimiento = :idProcedimiento)"),
        @NamedQuery(name = "ProcedimientoPaso.countHijos", query = "SELECT COUNT(s) FROM ProcedimientoPasoSecuencia s WHERE s.idProcedimientoPasoReferencia = :idPaso")
})
public class ProcedimientoPaso implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_procedimiento_paso")
    private UUID idProcedimientoPaso;

    @NotBlank(message = "El nombre del paso del procedimiento es obligatorio")
    @Size(max = 155, message = "El nombre no debe exceder los 155 caracteres")
    @Column(name = "nombre", nullable = false, length = 155)
    private String nombre;

    @NotNull(message = "El campo indicaFin es obligatorio")
    @Column(name = "indica_fin", nullable = false)
    private Boolean indicaFin = false;

    @OneToMany(mappedBy = "idProcedimientoPaso", fetch = FetchType.LAZY)
    private List<ProcedimientoPasoSecuencia> procedimientoPasoSecuenciaList;

    @OneToMany(mappedBy = "idProcedimientoPaso", fetch = FetchType.LAZY)
    private List<ProcedimientoPasoExamen> procedimientoPasoExamenList;

    @NotNull(message = "El procedimiento asociado es obligatorio")
    @JoinColumn(name = "id_procedimiento", referencedColumnName = "id_procedimiento", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Procedimiento idProcedimiento;

    @JoinColumn(name = "id_rol", referencedColumnName = "id_rol")
    @ManyToOne(fetch = FetchType.LAZY)
    private Rol idRol;

    public ProcedimientoPaso() {
    }

    public ProcedimientoPaso(UUID idProcedimientoPaso) {
        this.idProcedimientoPaso = idProcedimientoPaso;
    }

    public UUID getIdProcedimientoPaso() {
        return idProcedimientoPaso;
    }

    public void setIdProcedimientoPaso(UUID idProcedimientoPaso) {
        this.idProcedimientoPaso = idProcedimientoPaso;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = (nombre != null && !nombre.isBlank()) ? nombre.trim() : null;
    }

    public Boolean getIndicaFin() {
        return indicaFin;
    }

    public void setIndicaFin(Boolean indicaFin) {
        this.indicaFin = indicaFin;
    }

    public List<ProcedimientoPasoSecuencia> getProcedimientoPasoSecuenciaList() {
        return procedimientoPasoSecuenciaList;
    }

    public void setProcedimientoPasoSecuenciaList(List<ProcedimientoPasoSecuencia> procedimientoPasoSecuenciaList) {
        this.procedimientoPasoSecuenciaList = procedimientoPasoSecuenciaList;
    }

    public List<ProcedimientoPasoExamen> getProcedimientoPasoExamenList() {
        return procedimientoPasoExamenList;
    }

    public void setProcedimientoPasoExamenList(List<ProcedimientoPasoExamen> procedimientoPasoExamenList) {
        this.procedimientoPasoExamenList = procedimientoPasoExamenList;
    }

    public Procedimiento getIdProcedimiento() {
        return idProcedimiento;
    }

    public void setIdProcedimiento(Procedimiento idProcedimiento) {
        this.idProcedimiento = idProcedimiento;
    }

    public Rol getIdRol() {
        return idRol;
    }

    public void setIdRol(Rol idRol) {
        this.idRol = idRol;
    }

    @Override
    public int hashCode() {
        return (idProcedimientoPaso != null) ? idProcedimientoPaso.hashCode() : super.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof ProcedimientoPaso)) {
            return false;
        }
        ProcedimientoPaso other = (ProcedimientoPaso) object;
        if (this.idProcedimientoPaso == null || other.idProcedimientoPaso == null) {
            return false;
        }
        return Objects.equals(this.idProcedimientoPaso, other.idProcedimientoPaso);
    }

    @Override
    public String toString() {
        return "" + idProcedimientoPaso;
    }
}

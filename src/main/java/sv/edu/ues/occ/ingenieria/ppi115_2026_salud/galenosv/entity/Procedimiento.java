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
@Table(name = "procedimiento", schema = "public")
@NamedQueries({
    @NamedQuery(name = "Procedimiento.findAll", query = "SELECT p FROM Procedimiento p"),
    @NamedQuery(name = "Procedimiento.findByNombre", query = "SELECT p FROM Procedimiento p WHERE p.nombre = :nombre"),
    @NamedQuery(name = "Procedimiento.findByActivo", query = "SELECT p FROM Procedimiento p WHERE p.activo = :activo"),
    @NamedQuery(name = "Procedimiento.findByObservaciones", query = "SELECT p FROM Procedimiento p WHERE p.observaciones = :observaciones"),
    @NamedQuery(
            name = "Procedimiento.findActiveByNombre",
            query = "SELECT p FROM Procedimiento p "
            + "WHERE UPPER(p.nombre) LIKE UPPER(:nombre) "
            + "AND p.activo = true "
            + "ORDER BY p.nombre"
    )})
public class Procedimiento implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_procedimiento")
    private UUID idProcedimiento;

    @NotBlank(message = "El nombre del procedimiento es obligatorio")
    @Size(max = 155, message = "El nombre no debe exceder los 155 caracteres")
    @Column(name = "nombre", nullable = false, length = 155)
    private String nombre;

    @NotNull(message = "El estado activo es obligatorio")
    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @Size(max = 2000, message = "Las observaciones no deben exceder los 2000 caracteres")
    @Column(name = "observaciones", length = 2000)
    private String observaciones;

    @OneToMany(mappedBy = "idProcedimiento", fetch = FetchType.LAZY)
    private List<ProcedimientoPaso> procedimientoPasoList;

    public Procedimiento() {
    }

    public Procedimiento(UUID idProcedimiento) {
        this.idProcedimiento = idProcedimiento;
    }

    public UUID getIdProcedimiento() {
        return idProcedimiento;
    }

    public void setIdProcedimiento(UUID idProcedimiento) {
        this.idProcedimiento = idProcedimiento;
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

    public List<ProcedimientoPaso> getProcedimientoPasoList() {
        return procedimientoPasoList;
    }

    public void setProcedimientoPasoList(List<ProcedimientoPaso> procedimientoPasoList) {
        this.procedimientoPasoList = procedimientoPasoList;
    }

    @Override
    public int hashCode() {
        return (idProcedimiento != null) ? idProcedimiento.hashCode() : super.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Procedimiento)) {
            return false;
        }
        Procedimiento other = (Procedimiento) object;
        if (this.idProcedimiento == null || other.idProcedimiento == null) {
            return false;
        }
        return Objects.equals(this.idProcedimiento, other.idProcedimiento);
    }

    @Override
    public String toString() {
        return "sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Procedimiento[ idProcedimiento=" + idProcedimiento + " ]";
    }
}

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
@Table(name = "medio_contacto", schema = "public")
@NamedQueries({
    @NamedQuery(name = "MedioContacto.findAll", query = "SELECT m FROM MedioContacto m"),
    @NamedQuery(name = "MedioContacto.findByValor", query = "SELECT m FROM MedioContacto m WHERE m.valor = :valor"),
    @NamedQuery(name = "MedioContacto.findByFechaCreacion", query = "SELECT m FROM MedioContacto m WHERE m.fechaCreacion = :fechaCreacion"),
    @NamedQuery(name = "MedioContacto.findRangePadresHijos", query = "SELECT mc FROM MedioContacto mc LEFT JOIN FETCH mc.idPersona LEFT JOIN FETCH mc.idTipoMedioContacto ORDER BY mc.fechaCreacion DESC, mc.idMedioContacto"),
    @NamedQuery(name = "MedioContacto.buscarPadresHijos", query = "SELECT mc FROM MedioContacto mc LEFT JOIN FETCH mc.idPersona LEFT JOIN FETCH mc.idTipoMedioContacto WHERE mc.idMedioContacto = :id")
})
public class MedioContacto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_medio_contacto")
    private UUID idMedioContacto;

    @NotBlank(message = "El valor del medio de contacto es obligatorio")
    @Size(max = 255, message = "El valor no debe exceder los 255 caracteres")
    @Column(name = "valor", nullable = false, length = 255)
    private String valor;

    @NotNull(message = "La fecha de creación es obligatoria")
    @PastOrPresent(message = "La fecha de creación no puede ser una fecha futura")
    @Column(name = "fecha_creacion", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion = new Date();

    @NotNull(message = "La persona asociada es obligatoria")
    @JoinColumn(name = "id_persona", referencedColumnName = "id_persona", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Persona idPersona;

    @NotNull(message = "El tipo de medio de contacto es obligatorio")
    @JoinColumn(name = "id_tipo_medio_contacto", referencedColumnName = "id_tipo_medio_contacto", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private TipoMedioContacto idTipoMedioContacto;

    public MedioContacto() {
    }

    public MedioContacto(UUID idMedioContacto) {
        this.idMedioContacto = idMedioContacto;
    }

    public UUID getIdMedioContacto() {
        return idMedioContacto;
    }

    public void setIdMedioContacto(UUID idMedioContacto) {
        this.idMedioContacto = idMedioContacto;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = (valor != null && !valor.isBlank()) ? valor.trim() : null;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Persona getIdPersona() {
        return idPersona;
    }

    public void setIdPersona(Persona idPersona) {
        this.idPersona = idPersona;
    }

    public TipoMedioContacto getIdTipoMedioContacto() {
        return idTipoMedioContacto;
    }

    public void setIdTipoMedioContacto(TipoMedioContacto idTipoMedioContacto) {
        this.idTipoMedioContacto = idTipoMedioContacto;
    }

    @Override
    public int hashCode() {
        return (idMedioContacto != null) ? idMedioContacto.hashCode() : super.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof MedioContacto)) {
            return false;
        }
        MedioContacto other = (MedioContacto) object;
        if (this.idMedioContacto == null || other.idMedioContacto == null) {
            return false;
        }
        return Objects.equals(this.idMedioContacto, other.idMedioContacto);
    }

    @Override
    public String toString() {
        return "sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.MedioContacto[ idMedioContacto=" + idMedioContacto + " ]";
    }
}
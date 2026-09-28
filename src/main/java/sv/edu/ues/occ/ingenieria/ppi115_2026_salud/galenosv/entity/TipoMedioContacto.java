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
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@Entity
@Table(name = "tipo_medio_contacto", schema = "public")
@NamedQueries({
    @NamedQuery(name = "TipoMedioContacto.findAll", query = "SELECT t FROM TipoMedioContacto t"),
    @NamedQuery(name = "TipoMedioContacto.findByNombre", query = "SELECT t FROM TipoMedioContacto t WHERE t.nombre = :nombre"),
    @NamedQuery(name = "TipoMedioContacto.findByIndicaciones", query = "SELECT t FROM TipoMedioContacto t WHERE t.indicaciones = :indicaciones"),
    @NamedQuery(name = "TipoMedioContacto.findByExpresionRegular", query = "SELECT t FROM TipoMedioContacto t WHERE t.expresionRegular = :expresionRegular"),
    @NamedQuery(name = "TipoMedioContacto.findByActivo", query = "SELECT t FROM TipoMedioContacto t WHERE t.activo = :activo"),
    @NamedQuery(name = "TipoMedioContacto.findActiveByNombre", query = "SELECT t FROM TipoMedioContacto t WHERE UPPER(t.nombre) LIKE UPPER(:nombre) AND t.activo = true ORDER BY t.nombre")
})
public class TipoMedioContacto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_tipo_medio_contacto")
    private UUID idTipoMedioContacto;

    @NotBlank(message = "El nombre del tipo de medio de contacto es obligatorio")
    @Size(min = 3, max = 155, message = "El nombre debe tener entre 3 y 155 caracteres")
    @Column(name = "nombre", nullable = false, length = 155)
    private String nombre;

    @Size(max = 2000, message = "Las indicaciones no deben exceder los 2000 caracteres")
    @Column(name = "indicaciones", length = 2000)
    private String indicaciones;

    @Size(max = 500, message = "La expresión regular no debe exceder los 500 caracteres")
    @Column(name = "expresion_regular", length = 500)
    private String expresionRegular;

    @NotNull(message = "El estado activo es obligatorio")
    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @OneToMany(mappedBy = "idTipoMedioContacto", fetch = FetchType.LAZY)
    private List<MedioContacto> medioContactoList;

    public TipoMedioContacto() {
    }

    public TipoMedioContacto(UUID idTipoMedioContacto) {
        this.idTipoMedioContacto = idTipoMedioContacto;
    }

    @AssertTrue(message = "La expresión regular proporcionada no es un patrón sintácticamente válido")
    public boolean isExpresionRegularValida() {
        if (expresionRegular == null || expresionRegular.isBlank()) {
            return true;
        }
        try {
            Pattern.compile(expresionRegular);
            return true;
        } catch (PatternSyntaxException e) {
            return false;
        }
    }

    public UUID getIdTipoMedioContacto() {
        return idTipoMedioContacto;
    }

    public void setIdTipoMedioContacto(UUID idTipoMedioContacto) {
        this.idTipoMedioContacto = idTipoMedioContacto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = (nombre != null && !nombre.isBlank()) ? nombre.trim() : null;
    }

    public String getIndicaciones() {
        return indicaciones;
    }

    public void setIndicaciones(String indicaciones) {
        this.indicaciones = (indicaciones != null && !indicaciones.isBlank()) ? indicaciones.trim() : null;
    }

    public String getExpresionRegular() {
        return expresionRegular;
    }

    public void setExpresionRegular(String expresionRegular) {
        this.expresionRegular = (expresionRegular != null && !expresionRegular.isBlank()) ? expresionRegular.trim() : null;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public List<MedioContacto> getMedioContactoList() {
        return medioContactoList;
    }

    public void setMedioContactoList(List<MedioContacto> medioContactoList) {
        this.medioContactoList = medioContactoList;
    }

    @Override
    public int hashCode() {
        return (idTipoMedioContacto != null) ? idTipoMedioContacto.hashCode() : super.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof TipoMedioContacto)) {
            return false;
        }
        TipoMedioContacto other = (TipoMedioContacto) object;
        if (this.idTipoMedioContacto == null || other.idTipoMedioContacto == null) {
            return false;
        }
        return Objects.equals(this.idTipoMedioContacto, other.idTipoMedioContacto);
    }

    @Override
    public String toString() {
        return "sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.TipoMedioContacto[ idTipoMedioContacto=" + idTipoMedioContacto + " ]";
    }
    
    
}
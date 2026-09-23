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
@Table(name = "tipo_documento", schema = "public")
@NamedQueries({
    @NamedQuery(name = "TipoDocumento.findAll", query = "SELECT t FROM TipoDocumento t"),
    @NamedQuery(name = "TipoDocumento.findByNombre", query = "SELECT t FROM TipoDocumento t WHERE t.nombre = :nombre"),
    @NamedQuery(name = "TipoDocumento.findByIndicaciones", query = "SELECT t FROM TipoDocumento t WHERE t.indicaciones = :indicaciones"),
    @NamedQuery(name = "TipoDocumento.findByExpresionRegular", query = "SELECT t FROM TipoDocumento t WHERE t.expresionRegular = :expresionRegular"),
    @NamedQuery(name = "TipoDocumento.findByActivo", query = "SELECT t FROM TipoDocumento t WHERE t.activo = :activo")})
public class TipoDocumento implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_tipo_documento")
    private UUID idTipoDocumento;

    @NotBlank(message = "El nombre del tipo de documento es obligatorio")
    @Size(max = 155, message = "El nombre no debe exceder los 155 caracteres")
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

    @OneToMany(mappedBy = "idTipoDocumento", fetch = FetchType.LAZY)
    private List<Documento> documentoList;

    public TipoDocumento() {
    }

    public TipoDocumento(UUID idTipoDocumento) {
        this.idTipoDocumento = idTipoDocumento;
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

    public UUID getIdTipoDocumento() {
        return idTipoDocumento;
    }

    public void setIdTipoDocumento(UUID idTipoDocumento) {
        this.idTipoDocumento = idTipoDocumento;
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

    public List<Documento> getDocumentoList() {
        return documentoList;
    }

    public void setDocumentoList(List<Documento> documentoList) {
        this.documentoList = documentoList;
    }

    @Override
    public int hashCode() {
        return (idTipoDocumento != null) ? idTipoDocumento.hashCode() : super.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof TipoDocumento)) {
            return false;
        }
        TipoDocumento other = (TipoDocumento) object;
        if (this.idTipoDocumento == null || other.idTipoDocumento == null) {
            return false;
        }
        return Objects.equals(this.idTipoDocumento, other.idTipoDocumento);
    }

    @Override
    public String toString() {
        return "sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.TipoDocumento[ idTipoDocumento=" + idTipoDocumento + " ]";
    }
}
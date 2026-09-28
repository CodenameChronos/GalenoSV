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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "documento", schema = "public")
@NamedQueries({
    @NamedQuery(name = "Documento.findAll", query = "SELECT d FROM Documento d"),
    @NamedQuery(name = "Documento.findByValor", query = "SELECT d FROM Documento d WHERE d.valor = :valor"),
    @NamedQuery(name = "Documento.findByRutaFisica", query = "SELECT d FROM Documento d WHERE d.rutaFisica = :rutaFisica"),
    @NamedQuery(name = "Documento.findRangePadresHijos", query = "SELECT d FROM Documento d LEFT JOIN FETCH d.idPersona LEFT JOIN FETCH d.idTipoDocumento ORDER BY d.idDocumento DESC"),
    @NamedQuery(name = "Documento.buscarPadresHijos", query = "SELECT d FROM Documento d LEFT JOIN FETCH d.idPersona LEFT JOIN FETCH d.idTipoDocumento WHERE d.idDocumento = :id")
})
public class Documento implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_documento")
    private UUID idDocumento;

    @NotBlank(message = "El valor del documento es obligatorio")
    @Size(max = 255, message = "El valor del documento no debe exceder los 255 caracteres")
    @Column(name = "valor", nullable = false, length = 255)
    private String valor;

    @Size(max = 500, message = "La ruta física del documento no debe exceder los 500 caracteres")
    @Column(name = "ruta_fisica", length = 500)
    private String rutaFisica;

    @NotNull(message = "La persona asociada al documento es obligatoria")
    @JoinColumn(name = "id_persona", referencedColumnName = "id_persona", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Persona idPersona;

    @NotNull(message = "El tipo de documento es obligatorio")
    @JoinColumn(name = "id_tipo_documento", referencedColumnName = "id_tipo_documento", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private TipoDocumento idTipoDocumento;

    public Documento() {
    }

    public Documento(UUID idDocumento) {
        this.idDocumento = idDocumento;
    }

    public UUID getIdDocumento() {
        return idDocumento;
    }

    public void setIdDocumento(UUID idDocumento) {
        this.idDocumento = idDocumento;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = (valor != null && !valor.isBlank()) ? valor.trim() : null;
    }

    public String getRutaFisica() {
        return rutaFisica;
    }

    public void setRutaFisica(String rutaFisica) {
        this.rutaFisica = (rutaFisica != null && !rutaFisica.isBlank()) ? rutaFisica.trim() : null;
    }

    public Persona getIdPersona() {
        return idPersona;
    }

    public void setIdPersona(Persona idPersona) {
        this.idPersona = idPersona;
    }

    public TipoDocumento getIdTipoDocumento() {
        return idTipoDocumento;
    }

    public void setIdTipoDocumento(TipoDocumento idTipoDocumento) {
        this.idTipoDocumento = idTipoDocumento;
    }

    @Override
    public int hashCode() {
        return (idDocumento != null) ? idDocumento.hashCode() : super.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Documento)) {
            return false;
        }
        Documento other = (Documento) object;
        if (this.idDocumento == null || other.idDocumento == null) {
            return false;
        }
        return Objects.equals(this.idDocumento, other.idDocumento);
    }

    @Override
    public String toString() {
        return "sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Documento[ idDocumento=" + idDocumento + " ]";
    }
}
package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity;

import jakarta.persistence.Basic;
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
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "clinica", schema = "public")
@NamedQueries({
    @NamedQuery(name = "Clinica.findAll", query = "SELECT c FROM Clinica c"),
    @NamedQuery(name = "Clinica.findByNombre", query = "SELECT c FROM Clinica c WHERE c.nombre = :nombre"),
    @NamedQuery(name = "Clinica.findByActivo", query = "SELECT c FROM Clinica c WHERE c.activo = :activo"),
    @NamedQuery(name = "Clinica.findByTipo", query = "SELECT c FROM Clinica c WHERE c.tipo = :tipo"),
    @NamedQuery(name = "Clinica.findByComentarios", query = "SELECT c FROM Clinica c WHERE c.comentarios = :comentarios")})
public class Clinica implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_clinica")
    private UUID idClinica;

    @Basic(optional = false)
    @NotBlank(message = "El nombre de la clínica es obligatorio")
    @Size(max = 255, message = "El nombre no debe exceder los 255 caracteres")
    @Column(name = "nombre", nullable = false, length = 255)
    private String nombre;

    @NotNull(message = "El estado de la clínica es obligatorio")
    @Column(name = "activo", nullable = false)
    private Boolean activo = Boolean.TRUE; // Inicialización única

    // Opción A: Si 'tipo' es un conjunto cerrado de opciones (Ajusta la expresión regular según tus tipos reales)
    @Size(max = 20, message = "El tipo de clínica no debe exceder los 20 caracteres")
    @Column(name = "tipo", length = 20)
    private String tipo;

    @Size(max = 2000, message = "Los comentarios no deben exceder los 2000 caracteres")
    @Column(name = "comentarios", columnDefinition = "TEXT")
    private String comentarios;

    @OneToMany(mappedBy = "idClinica", fetch = FetchType.LAZY)
    private List<PersonaRol> personaRolList;

    public Clinica() {
    }

    public Clinica(UUID idClinica) {
        this.idClinica = idClinica;
    }

    public Clinica(UUID idClinica, String nombre) {
        this.idClinica = idClinica;
        this.nombre = nombre;
    }

    public UUID getIdClinica() {
        return idClinica;
    }

    public void setIdClinica(UUID idClinica) {
        this.idClinica = idClinica;
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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        // Texto libre: limpia espacios y convierte vacíos ("   ") a null
        this.tipo = (tipo != null && !tipo.isBlank()) ? tipo.trim() : null;
    }

    public String getComentarios() {
        return comentarios;
    }

    public void setComentarios(String comentarios) {
        this.comentarios = (comentarios != null && !comentarios.isBlank()) ? comentarios.trim() : null;
    }

    public List<PersonaRol> getPersonaRolList() {
        return personaRolList;
    }

    public void setPersonaRolList(List<PersonaRol> personaRolList) {
        this.personaRolList = personaRolList;
    }

    // Mejora en equals() y hashCode() para evitar colisiones en entidades no persistidas (sin ID)
    @Override
    public int hashCode() {
        return (idClinica != null) ? idClinica.hashCode() : super.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Clinica)) {
            return false;
        }
        Clinica other = (Clinica) object;
        if (this.idClinica == null || other.idClinica == null) {
            return false; // Entidades sin ID no son iguales entre sí a menos que sean la misma instancia
        }
        return Objects.equals(this.idClinica, other.idClinica);
    }

    @Override
    public String toString() {
        return "sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Clinica[ idClinica=" + idClinica + " ]";
    }
}
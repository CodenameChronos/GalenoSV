package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DefaultDAOTest {

    private EntityManager entityManager;
    private TypedQuery<String> query;
    private DefaultDAOImpl dao;

    @BeforeEach
    public void setUp() {
        entityManager = mock(EntityManager.class);
        query = mock(TypedQuery.class);
        dao = new DefaultDAOImpl(String.class, entityManager);
    }

    @Test
    public void testGetEntityManager() {
        assertEquals(entityManager, dao.getEntityManager());
    }

    @Test
    public void testCrear() {
        String registro = "Registro";

        dao.crear(registro);

        verify(entityManager).persist(registro);
    }

    @Test
    public void testCrearConRegistroNulo() {
        assertThrows(
                IllegalArgumentException.class,
                () -> dao.crear(null)
        );

        verifyNoInteractions(entityManager);
    }

    @Test
    public void testActualizar() {
        String registro = "Registro";

        dao.actualizar(registro);

        verify(entityManager).merge(registro);
    }

    @Test
    public void testActualizarConRegistroNulo() {
        assertThrows(
                IllegalArgumentException.class,
                () -> dao.actualizar(null)
        );

        verifyNoInteractions(entityManager);
    }

    @Test
    public void testEliminar() {
        String registro = "Registro";

        dao.eliminar(registro);

        verify(entityManager).remove(registro);
    }

    @Test
    public void testEliminarConRegistroNulo() {
        assertThrows(
                IllegalArgumentException.class,
                () -> dao.eliminar(null)
        );

        verifyNoInteractions(entityManager);
    }

    @Test
    public void testBuscar() {
        String id = "123";
        String esperado = "Registro encontrado";

        when(entityManager.find(String.class, id)).thenReturn(esperado);

        Object resultado = dao.buscar(id);

        assertEquals(esperado, resultado);
        verify(entityManager).find(String.class, id);
    }

    @Test
    public void testBuscarConValorNulo() {
        assertThrows(
                IllegalArgumentException.class,
                () -> dao.buscar(null)
        );

        verifyNoInteractions(entityManager);
    }

    @Test
    public void testFindRange() {
        List<String> registros = List.of("A", "B", "C");

        when(entityManager.createQuery(
                "SELECT e FROM String e",
                String.class
        )).thenReturn(query);

        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(3)).thenReturn(query);
        when(query.getResultList()).thenReturn(registros);

        List<String> resultado = dao.findRange(0, 3);

        assertEquals(registros, resultado);

        verify(entityManager).createQuery(
                "SELECT e FROM String e",
                String.class
        );
        verify(query).setFirstResult(0);
        verify(query).setMaxResults(3);
        verify(query).getResultList();
    }

    @Test
    public void testFindRangeConParametrosInvalidos() {
        assertThrows(
                IllegalArgumentException.class,
                () -> dao.findRange(-1, 3)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> dao.findRange(0, 0)
        );

        verifyNoInteractions(entityManager);
    }

    @Test
    public void testContar() {
        when(entityManager.createQuery(
                "SELECT COUNT(*) e FROM String e",
                String.class
        )).thenReturn(query);

        when(query.getMaxResults()).thenReturn(10);

        int resultado = dao.contar();

        assertEquals(10, resultado);

        verify(entityManager).createQuery(
                "SELECT COUNT(*) e FROM String e",
                String.class
        );
        verify(query).getMaxResults();
    }

    private static class DefaultDAOImpl extends DefaultDAO<String> {

        private final EntityManager entityManager;

        public DefaultDAOImpl(
                Class<String> entity,
                EntityManager entityManager
        ) {
            super(entity);
            this.entityManager = entityManager;
        }

        @Override
        public EntityManager getEntityManager() {
            return entityManager;
        }
    }
}
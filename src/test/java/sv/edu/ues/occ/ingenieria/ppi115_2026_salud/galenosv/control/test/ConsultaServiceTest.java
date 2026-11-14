package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ConsultaService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Clinica;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Consulta;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsultaServiceTest {

    @Mock
    private EntityManager emMock;

    @Mock
    private TypedQuery<Consulta> consultaQueryMock;

    @Mock
    private TypedQuery<Long> countQueryMock;

    @InjectMocks
    private ConsultaService service;

    private Clinica clinica;
    private Date desde;
    private Date hasta;
    private Consulta consulta;

    @BeforeEach
    void setUp() {
        clinica = new Clinica();
        desde = new Date();
        hasta = new Date();
        consulta = new Consulta();
    }

    // ==========================================
    // PRUEBAS PARA buscarPorClinicaYFechas
    // ==========================================

    @Test
    @DisplayName("buscarPorClinicaYFechas - Éxito al obtener consultas en rango de fechas")
    void buscarPorClinicaYFechas_Success() {
        List<Consulta> lista = List.of(consulta);
        when(emMock.createNamedQuery("Consulta.buscarPorClinicaYFechas", Consulta.class))
                .thenReturn(consultaQueryMock);
        when(consultaQueryMock.setParameter("clinica", clinica)).thenReturn(consultaQueryMock);
        when(consultaQueryMock.setParameter("desde", desde)).thenReturn(consultaQueryMock);
        when(consultaQueryMock.setParameter("hasta", hasta)).thenReturn(consultaQueryMock);
        when(consultaQueryMock.setFirstResult(0)).thenReturn(consultaQueryMock);
        when(consultaQueryMock.setMaxResults(10)).thenReturn(consultaQueryMock);
        when(consultaQueryMock.getResultList()).thenReturn(lista);

        List<Consulta> resultado = service.buscarPorClinicaYFechas(clinica, desde, hasta, 0, 10);

        assertEquals(lista, resultado);
        verify(consultaQueryMock, times(1)).setParameter("clinica", clinica);
    }

    @Test
    @DisplayName("buscarPorClinicaYFechas - Fallo en consulta (Propaga excepción)")
    void buscarPorClinicaYFechas_QueryError_ThrowsException() {
        when(emMock.createNamedQuery("Consulta.buscarPorClinicaYFechas", Consulta.class))
                .thenThrow(new RuntimeException("Error JPQL"));

        assertThrows(RuntimeException.class,
                () -> service.buscarPorClinicaYFechas(clinica, desde, hasta, 0, 10));
    }

    // ==========================================
    // PRUEBAS PARA contarPorClinicaYFechas
    // ==========================================

    @Test
    @DisplayName("contarPorClinicaYFechas - Éxito al contar consultas en rango de fechas")
    void contarPorClinicaYFechas_Success() {
        when(emMock.createNamedQuery("Consulta.contarPorClinicaYFechas", Long.class))
                .thenReturn(countQueryMock);
        when(countQueryMock.setParameter("clinica", clinica)).thenReturn(countQueryMock);
        when(countQueryMock.setParameter("desde", desde)).thenReturn(countQueryMock);
        when(countQueryMock.setParameter("hasta", hasta)).thenReturn(countQueryMock);
        when(countQueryMock.getSingleResult()).thenReturn(8L);

        long resultado = service.contarPorClinicaYFechas(clinica, desde, hasta);

        assertEquals(8L, resultado);
    }

    @Test
    @DisplayName("contarPorClinicaYFechas - Fallo en consulta (Propaga excepción)")
    void contarPorClinicaYFechas_QueryError_ThrowsException() {
        when(emMock.createNamedQuery("Consulta.contarPorClinicaYFechas", Long.class))
                .thenThrow(new RuntimeException("Error de conteo"));

        assertThrows(RuntimeException.class,
                () -> service.contarPorClinicaYFechas(clinica, desde, hasta));
    }
}
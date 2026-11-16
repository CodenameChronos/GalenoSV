package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf.test;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.*;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf.ConsultaProcedimientoPasoModel;

@ExtendWith(MockitoExtension.class)
class ConsultaProcedimientoPasoModelTest {

    @Mock
    private ConsultaProcedimientoService conspServiceMock;

    @Mock
    private PersonaRolService prServiceMock;

    private ConsultaProcedimientoPasoModel consultaProcedimientoPasoModel;

    private UUID sampleId;
    private ConsultaProcedimientoPaso sampleConsultaProcedimientoPaso;

    @BeforeEach
    void setUp() {
        sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf.test.ModelTestSupport.crearModelo(this, ConsultaProcedimientoPasoModel.class, "consultaProcedimientoPasoModel");
        sampleId = UUID.randomUUID();
        sampleConsultaProcedimientoPaso = new ConsultaProcedimientoPaso();
        sampleConsultaProcedimientoPaso.setIdConsultaProcedimientoPaso(sampleId);
    }

    @Test
    @DisplayName("buscarConsultaProcedimiento - Éxito (Busca con el texto sin espacios sobrantes)")
    void buscarConsultaProcedimiento_Success() {
        List<ConsultaProcedimiento> encontrados = List.of(new ConsultaProcedimiento(UUID.randomUUID()));
        when(conspServiceMock.buscarPorNombreProcedimiento("limpieza", Integer.MAX_VALUE)).thenReturn(encontrados);
        consultaProcedimientoPasoModel.setFiltroConsultaProcedimiento("  limpieza  ");

        consultaProcedimientoPasoModel.buscarConsultaProcedimiento();

        assertSame(encontrados, consultaProcedimientoPasoModel.getResultadosConsultaProcedimiento());
        // Ya había resultados: el getter no vuelve a consultar
        verify(conspServiceMock, times(1)).buscarPorNombreProcedimiento(any(), anyInt());
    }

    @Test
    @DisplayName("buscarConsultaProcedimiento - Fallos (Filtro nulo se trata como texto vacío)")
    void buscarConsultaProcedimiento_Failures() {
        when(conspServiceMock.buscarPorNombreProcedimiento("", Integer.MAX_VALUE)).thenReturn(List.of());
        consultaProcedimientoPasoModel.setFiltroConsultaProcedimiento(null);

        consultaProcedimientoPasoModel.buscarConsultaProcedimiento();

        assertTrue(consultaProcedimientoPasoModel.getResultadosConsultaProcedimiento().isEmpty());
    }
    // ==========================================
    // PRUEBAS PARA buscarPersonaRol
    // ==========================================

    @Test
    @DisplayName("buscarPersonaRol - Éxito (Busca con el texto sin espacios sobrantes)")
    void buscarPersonaRol_Success() {
        List<PersonaRol> encontrados = List.of(new PersonaRol(UUID.randomUUID()));
        when(prServiceMock.buscarPorNombresApellidos("ana", Integer.MAX_VALUE)).thenReturn(encontrados);
        consultaProcedimientoPasoModel.setFiltroPersonaRol("  ana ");

        consultaProcedimientoPasoModel.buscarPersonaRol();

        assertSame(encontrados, consultaProcedimientoPasoModel.getResultadosPersonaRol());
        verify(prServiceMock, times(1)).buscarPorNombresApellidos(any(), anyInt());
    }

    @Test
    @DisplayName("buscarPersonaRol - Fallos (Filtro nulo se trata como texto vacío)")
    void buscarPersonaRol_Failures() {
        when(prServiceMock.buscarPorNombresApellidos("", Integer.MAX_VALUE)).thenReturn(List.of());
        consultaProcedimientoPasoModel.setFiltroPersonaRol(null);

        consultaProcedimientoPasoModel.buscarPersonaRol();

        assertTrue(consultaProcedimientoPasoModel.getResultadosPersonaRol().isEmpty());
    }
}

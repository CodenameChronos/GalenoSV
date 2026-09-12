/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.resources;

import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.TipoMedioContactoDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.Mockito;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.TipoMedioContacto;

/**
 *
 * @author kardia
 */
/*
public class TipoMedioContactoDAOTest {
    
    List<TipoMedioContacto> LISTA_REGISTROS;
    
    public TipoMedioContactoDAOTest() {
        LISTA_REGISTROS = new ArrayList<>();
        LISTA_REGISTROS.add(new TipoMedioContacto(UUID.randomUUID()));
        LISTA_REGISTROS.add(new TipoMedioContacto(UUID.randomUUID()));
        LISTA_REGISTROS.add(new TipoMedioContacto(UUID.randomUUID()));
    }

    @Test
    public void testFindRange() {
        System.out.println("findRange");
        int first = 0;
        int max = 100;
        int esperado = LISTA_REGISTROS.size();
        EntityManager mockEM = Mockito.mock(EntityManager.class);
        TypedQuery<TipoMedioContacto> mockTQ = Mockito.mock(TypedQuery.class);
        Mockito.when(mockTQ.getResultList()).thenReturn(LISTA_REGISTROS);
        Mockito.when(mockEM.createNamedQuery(
                    "TipoMedioContacto.findAll", TipoMedioContacto.class
        )).thenReturn(mockTQ);
        // cut = class under test
        //TipoMedioContactoDAO cut = new TipoMedioContactoDAO();
        //List<TipoMedioContacto> resultado = cut.findRange(first, max);
        //assertNotNull(resultado);
        //assertEquals(esperado, resultado.size());
        
        //fail("The test case is a prototype.");
    }
    
}
*/

package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import jakarta.inject.Inject;
import static org.mockito.Mockito.mockingDetails;

/**
 * Utilidad de pruebas para construir modelos JSF e inyectar únicamente
 * los mocks declarados en cada clase de prueba.
 *
 * Evita @InjectMocks porque ModelHandler contiene campos de tipo Object
 * que pueden provocar ambigüedad en la inyección automática de Mockito.
 */
public final class ModelTestSupport {

    private ModelTestSupport() {
    }

    public static <T> T crearModelo(Object test, Class<T> tipoModelo, String nombreCampoModelo) {
        try {
            T modelo = tipoModelo.getDeclaredConstructor().newInstance();

            Field campoModelo = buscarCampo(test.getClass(), nombreCampoModelo);
            campoModelo.setAccessible(true);
            campoModelo.set(test, modelo);

            for (Class<?> tipo = tipoModelo; tipo != null && tipo != Object.class; tipo = tipo.getSuperclass()) {
                for (Field dependencia : tipo.getDeclaredFields()) {
                    if (!dependencia.isAnnotationPresent(Inject.class)) {
                        continue;
                    }

                    Field mock = buscarMock(test.getClass(), dependencia);
                    if (mock != null) {
                        mock.setAccessible(true);
                        dependencia.setAccessible(true);
                        dependencia.set(modelo, mock.get(test));
                    }
                }
            }
            return modelo;
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException(
                    "No fue posible inicializar " + tipoModelo.getSimpleName()
                    + " para la prueba. Verifique el constructor y los mocks declarados.", ex);
        }
    }

    private static Field buscarMock(Class<?> claseTest, Field dependencia) {
        List<Field> compatibles = new ArrayList<>();

        for (Class<?> tipo = claseTest; tipo != null && tipo != Object.class; tipo = tipo.getSuperclass()) {
            for (Field candidato : tipo.getDeclaredFields()) {
                if (!dependencia.getType().isAssignableFrom(candidato.getType())) {
                    continue;
                }
                compatibles.add(candidato);
            }
        }

        for (Field candidato : compatibles) {
            if (candidato.getName().equals(dependencia.getName())
                    && candidato.isAnnotationPresent(org.mockito.Mock.class)) {
                return candidato;
            }
        }

        List<Field> mocks = new ArrayList<>();
        for (Field candidato : compatibles) {
            if (candidato.isAnnotationPresent(org.mockito.Mock.class)) {
                mocks.add(candidato);
            }
        }

        return mocks.size() == 1 ? mocks.get(0) : null;
    }

    private static Field buscarCampo(Class<?> clase, String nombre) throws NoSuchFieldException {
        for (Class<?> tipo = clase; tipo != null && tipo != Object.class; tipo = tipo.getSuperclass()) {
            try {
                return tipo.getDeclaredField(nombre);
            } catch (NoSuchFieldException ignored) {
            }
        }
        throw new NoSuchFieldException(nombre);
    }
}

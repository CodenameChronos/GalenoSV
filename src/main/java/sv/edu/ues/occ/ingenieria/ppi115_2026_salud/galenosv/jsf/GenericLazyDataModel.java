package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf;

import java.util.List;
import java.util.Map;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.SortMeta;
import org.primefaces.model.LazyDataModel;

public class GenericLazyDataModel<T> extends LazyDataModel<T> {

    private static final long serialVersionUID = 1L;

    private final ModelHandler<T> modelHandler;

    public GenericLazyDataModel(ModelHandler<T> modelHandler) {
        this.modelHandler = modelHandler;
    }

    @Override
    public int count(Map<String, FilterMeta> filterBy) {
        return modelHandler.contar();
    }

    @Override
    public List<T> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        return modelHandler.buscarRegistros(first, pageSize);
    }

    @Override
    public String getRowKey(T registro) {
        Object id = modelHandler.getIdByRegistro(registro);
        return id != null ? id.toString() : null;
    }

    @Override
    public T getRowData(String rowKey) {
        return modelHandler.getRegistroById(rowKey);
    }
}

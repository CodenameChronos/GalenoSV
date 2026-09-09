package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.UUID;

@Converter
public class UUIDConverter implements AttributeConverter<UUID, UUID> {

    @Override
    public UUID convertToDatabaseColumn(UUID x) {
        return x;
    }

    @Override
    public UUID convertToEntityAttribute(UUID y) {
        return y;
    }

    
    
}

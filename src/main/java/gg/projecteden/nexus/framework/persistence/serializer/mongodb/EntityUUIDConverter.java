package gg.projecteden.nexus.framework.persistence.serializer.mongodb;

import dev.morphia.converters.SimpleValueConverter;
import dev.morphia.converters.TypeConverter;
import dev.morphia.mapping.MappedField;
import dev.morphia.mapping.Mapper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Snowball;

import java.util.UUID;

public class EntityUUIDConverter extends TypeConverter implements SimpleValueConverter {

	public EntityUUIDConverter(Mapper mapper) {
		super(Entity.class, Snowball.class);
	}

	@Override
	public Object encode(Object value, MappedField optionalExtraInfo) {
		if (!(value instanceof Entity entity)) return null;
		return entity.getUniqueId().toString();
	}

	@Override
	public Object decode(Class<?> aClass, Object value, MappedField mappedField) {
		if (!(value instanceof String string)) return null;
		return Bukkit.getEntity(UUID.fromString(string));
	}

}

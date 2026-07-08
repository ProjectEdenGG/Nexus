package gg.projecteden.nexus.framework.persistence.serializer.mongodb;

import dev.morphia.converters.SimpleValueConverter;
import dev.morphia.converters.TypeConverter;
import dev.morphia.mapping.MappedField;
import dev.morphia.mapping.Mapper;
import org.bukkit.Material;

public class MaterialConverter extends TypeConverter implements SimpleValueConverter {

	public MaterialConverter(Mapper mapper) {
		super(Material.class);
	}

	@Override
	public Object encode(Object value, MappedField optionalExtraInfo) {
		if (!(value instanceof Material material)) return null;
		return material.name();
	}

	@Override
	public Object decode(Class<?> aClass, Object value, MappedField mappedField) {
		if (!(value instanceof String string)) return null;

		if ("CHAIN".equals(string))
			return Material.IRON_CHAIN;

		return Material.valueOf(string);
	}

}

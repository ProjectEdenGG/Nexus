package gg.projecteden.nexus.framework.persistence.serializer.mongodb;

import dev.morphia.converters.SimpleValueConverter;
import dev.morphia.converters.TypeConverter;
import dev.morphia.mapping.MappedField;
import dev.morphia.mapping.Mapper;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;

public class WorldConverter extends TypeConverter implements SimpleValueConverter {

	public WorldConverter(Mapper mapper) {
		super(World.class, CraftWorld.class);
	}

	@Override
	public Object encode(Object value, MappedField optionalExtraInfo) {
		if (!(value instanceof World world)) return null;
		return world.getName();
	}

	@Override
	public Object decode(Class<?> aClass, Object value, MappedField mappedField) {
		if (value == null) return null;
		return Bukkit.getWorld((String) value);
	}

}

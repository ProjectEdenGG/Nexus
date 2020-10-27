package me.pugabyte.bncore.framework.persistence.serializer.mongodb;

import dev.morphia.converters.SimpleValueConverter;
import dev.morphia.converters.TypeConverter;
import dev.morphia.mapping.MappedField;
import dev.morphia.mapping.Mapper;

import java.util.UUID;

public class UUIDConverter extends TypeConverter implements SimpleValueConverter {

	public UUIDConverter(Mapper mapper) {
		super(UUID.class);
	}

	@Override
	public Object encode(Object value, MappedField optionalExtraInfo) {
		return value.toString();
	}

	@Override
	public Object decode(Class<?> aClass, Object value, MappedField mappedField) {
		return UUID.fromString((String) value);
	}

}

package me.pugabyte.bncore.framework.persistence.serializer.mongodb;

import dev.morphia.converters.TypeConverter;
import dev.morphia.mapping.MappedField;

import java.util.UUID;

public class UUIDConverter extends TypeConverter {

	public UUIDConverter() {
		super(UUID.class);
	}

	@Override
	public Object encode(Object value, MappedField optionalExtraInfo) {
		return value.toString();
	}

	@Override
	public Object decode(Class<?> aClass, Object object, MappedField mappedField) {
		return UUID.fromString((String) object);
	}

}

package me.pugabyte.nexus.framework.persistence.serializer.mongodb;

import dev.morphia.converters.SimpleValueConverter;
import dev.morphia.converters.TypeConverter;
import dev.morphia.mapping.MappedField;
import dev.morphia.mapping.Mapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeConverter extends TypeConverter implements SimpleValueConverter {

	public LocalDateTimeConverter(Mapper mapper) {
		super(LocalDateTime.class);
	}

	@Override
	public Object encode(Object value, MappedField optionalExtraInfo) {
		if (value == null) return null;
		return ((LocalDateTime) value).format(DateTimeFormatter.ISO_DATE_TIME);
	}

	@Override
	public Object decode(Class<?> aClass, Object value, MappedField mappedField) {
		if (value == null) return null;
		return LocalDateTime.parse((String) value);
	}

}

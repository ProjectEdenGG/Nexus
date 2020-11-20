package me.pugabyte.nexus.framework.persistence.serializer.mongodb;

import dev.morphia.converters.SimpleValueConverter;
import dev.morphia.converters.TypeConverter;
import dev.morphia.mapping.MappedField;
import dev.morphia.mapping.Mapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateConverter extends TypeConverter implements SimpleValueConverter {

	public LocalDateConverter(Mapper mapper) {
		super(LocalDate.class);
	}

	@Override
	public Object encode(Object value, MappedField optionalExtraInfo) {
		if (value == null) return null;
		return ((LocalDate) value).format(DateTimeFormatter.ISO_DATE);
	}

	@Override
	public Object decode(Class<?> aClass, Object value, MappedField mappedField) {
		if (value == null) return null;
		return LocalDate.parse((String) value);
	}

}

package me.pugabyte.nexus.framework.persistence.serializer.mongodb;

import dev.morphia.converters.SimpleValueConverter;
import dev.morphia.converters.TypeConverter;
import dev.morphia.mapping.MappedField;
import dev.morphia.mapping.Mapper;

import java.math.BigDecimal;

public class BigDecimalConverter extends TypeConverter implements SimpleValueConverter {

	public BigDecimalConverter(Mapper mapper) {
		super(BigDecimal.class);
	}

	@Override
	public Object encode(Object value, MappedField optionalExtraInfo) {
		if (value == null) return null;
		return ((BigDecimal) value).doubleValue();
	}

	@Override
	public Object decode(Class<?> aClass, Object value, MappedField mappedField) {
		if (value == null) return null;
		return BigDecimal.valueOf((Double) value);
	}

}

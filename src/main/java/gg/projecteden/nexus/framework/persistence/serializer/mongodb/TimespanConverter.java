package gg.projecteden.nexus.framework.persistence.serializer.mongodb;

import dev.morphia.converters.SimpleValueConverter;
import dev.morphia.converters.TypeConverter;
import dev.morphia.mapping.MappedField;
import dev.morphia.mapping.Mapper;
import gg.projecteden.utils.TimeUtils.Timespan;

public class TimespanConverter extends TypeConverter implements SimpleValueConverter {

	public TimespanConverter(Mapper mapper) {
		super(Timespan.class);
	}

	@Override
	public Object encode(Object value, MappedField optionalExtraInfo) {
		if (value == null) return null;
		return encode((Timespan) value);
	}

	public static long encode(Timespan timespan) {
		return timespan.getOriginal();
	}

	@Override
	public Object decode(Class<?> aClass, Object value, MappedField mappedField) {
		if (value == null) return null;
		return decode((long) value);
	}

	public static Timespan decode(long value) {
		return Timespan.ofMillis(value);
	}

}

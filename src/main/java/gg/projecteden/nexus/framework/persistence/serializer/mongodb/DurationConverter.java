package gg.projecteden.nexus.framework.persistence.serializer.mongodb;

import dev.morphia.converters.SimpleValueConverter;
import dev.morphia.converters.TypeConverter;
import dev.morphia.mapping.MappedField;
import dev.morphia.mapping.Mapper;

import java.time.Duration;
import java.util.Map;

public class DurationConverter extends TypeConverter implements SimpleValueConverter {

	public DurationConverter(Mapper mapper) {
		super(Duration.class);
	}

	@Override
	public Object encode(Object value, MappedField optionalExtraInfo) {
		if (value == null) return null;
		return encode((Duration) value);
	}

	public static Map<String, Number> encode(Duration duration) {
		return Map.of(
			"seconds", duration.getSeconds(),
			"nanos", duration.getNano()
		);
	}

	@Override
	public Object decode(Class<?> aClass, Object value, MappedField mappedField) {
		if (value == null) return null;
		return decode((Map<String, Number>) value);
	}

	public static Duration decode(Map<String, Number> value) {
		if (!(value.containsKey("seconds") && value.containsKey("nanos"))) return null;
		return Duration.ofSeconds(value.get("seconds").longValue(), value.get("nanos").longValue());
	}

}

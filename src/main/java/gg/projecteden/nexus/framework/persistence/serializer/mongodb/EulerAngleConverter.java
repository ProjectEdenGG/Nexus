package gg.projecteden.nexus.framework.persistence.serializer.mongodb;

import com.mongodb.BasicDBObject;
import dev.morphia.converters.SimpleValueConverter;
import dev.morphia.converters.TypeConverter;
import dev.morphia.mapping.MappedField;
import dev.morphia.mapping.Mapper;
import lombok.SneakyThrows;
import org.bukkit.util.EulerAngle;

import java.util.HashMap;
import java.util.Map;

public class EulerAngleConverter extends TypeConverter implements SimpleValueConverter {

	public EulerAngleConverter(Mapper mapper) {
		super(EulerAngle.class);
	}

	@Override
	public Object encode(Object value, MappedField optionalExtraInfo) {
		if (value == null) return null;
		return new BasicDBObject(new HashMap<String, Double>() {{
			put("x", ((EulerAngle) value).getX());
			put("y", ((EulerAngle) value).getY());
			put("z", ((EulerAngle) value).getZ());
		}});
	}

	@Override
	@SneakyThrows
	public Object decode(Class<?> aClass, Object value, MappedField mappedField) {
		if (value == null) return null;
		Map<String, Double> angle = ((BasicDBObject) value).toMap();
		return new EulerAngle(angle.get("x"), angle.get("y"), angle.get("z"));
	}

}

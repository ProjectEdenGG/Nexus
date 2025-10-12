package gg.projecteden.nexus.framework.persistence.serializer.mongodb;

import com.mongodb.BasicDBObject;
import dev.morphia.converters.SimpleValueConverter;
import dev.morphia.converters.TypeConverter;
import dev.morphia.mapping.MappedField;
import dev.morphia.mapping.Mapper;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class JavaColorConverter extends TypeConverter implements SimpleValueConverter {

	public JavaColorConverter(Mapper mapper) {
		super(Color.class);
	}

	@Override
	public Object encode(Object value, MappedField optionalExtraInfo) {
		if (value == null) return null;
		return new BasicDBObject(new HashMap<String, Integer>() {{
			put("r", ((Color) value).getRed());
			put("g", ((Color) value).getGreen());
			put("b", ((Color) value).getBlue());
		}});
	}

	@Override
	public Object decode(Class<?> aClass, Object value, MappedField mappedField) {
		if (value == null) return null;
		Map<String, Integer> color = ((BasicDBObject) value).toMap();
		return new Color(color.get("r"), color.get("g"), color.get("b"));
	}

}

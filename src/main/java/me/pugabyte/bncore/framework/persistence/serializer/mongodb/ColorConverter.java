package me.pugabyte.bncore.framework.persistence.serializer.mongodb;

import com.mongodb.BasicDBObject;
import dev.morphia.converters.TypeConverter;
import dev.morphia.mapping.MappedField;
import org.bukkit.Color;

import java.util.HashMap;
import java.util.Map;

public class ColorConverter extends TypeConverter {

	public ColorConverter() {
		super(Color.class);
	}

	@Override
	public Object encode(Object value, MappedField optionalExtraInfo) {
		if (value == null)
			return null;

		return new BasicDBObject(new HashMap<String, Integer>() {{
			put("r", ((Color) value).getRed());
			put("g", ((Color) value).getGreen());
			put("b", ((Color) value).getBlue());
		}});
	}

	@Override
	public Object decode(Class<?> aClass, Object value, MappedField mappedField) {
		Map<String, Integer> color = ((BasicDBObject) value).toMap();
		return Color.fromRGB(color.get("r"), color.get("g"), color.get("b"));
	}

}

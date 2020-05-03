package me.pugabyte.bncore.framework.persistence.serializer.mongodb;

import com.mongodb.BasicDBObject;
import dev.morphia.converters.TypeConverter;
import dev.morphia.mapping.MappedField;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class LocationConverter extends TypeConverter {

	public LocationConverter() {
		super(Location.class);
	}

	@Override
	public Object encode(Object value, MappedField optionalExtraInfo) {
		Location location = (Location) value;
		if (location.getWorld() == null)
			return null;
		return new BasicDBObject() {{
			put("world", location.getWorld().getName());
			put("x", BigDecimal.valueOf(location.getX()).setScale(3, RoundingMode.HALF_UP).doubleValue());
			put("y", BigDecimal.valueOf(location.getY()).setScale(3, RoundingMode.HALF_UP).doubleValue());
			put("z", BigDecimal.valueOf(location.getZ()).setScale(3, RoundingMode.HALF_UP).doubleValue());
			put("yaw", BigDecimal.valueOf(location.getYaw()).setScale(3, RoundingMode.HALF_UP).doubleValue());
			put("pitch", BigDecimal.valueOf(location.getPitch()).setScale(3, RoundingMode.HALF_UP).doubleValue());
		}};
	}

	@Override
	public Object decode(Class<?> aClass, Object location, MappedField mappedField) {
		BasicDBObject deserialized = (BasicDBObject) location;
		return new Location(
				Bukkit.getWorld(deserialized.getString("world")),
				deserialized.getDouble("x"),
				deserialized.getDouble("y"),
				deserialized.getDouble("z"),
				Double.valueOf(deserialized.getDouble("yaw")).floatValue(),
				Double.valueOf(deserialized.getDouble("pitch")).floatValue()
		);
	}

}

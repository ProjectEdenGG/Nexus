package me.pugabyte.bncore.framework.persistence.serializer.mongodb;

import com.mongodb.BasicDBObject;
import dev.morphia.converters.TypeConverter;
import dev.morphia.mapping.MappedField;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationConverter extends TypeConverter {

	public LocationConverter() {
		super(Location.class);
	}

	@Override
	public Object encode(Object value, MappedField optionalExtraInfo) {
		Location location = (Location) value;
		return new BasicDBObject() {{
			put("world", location.getWorld().getName());
			put("x", location.getX());
			put("y", location.getY());
			put("z", location.getZ());
			put("yaw", location.getYaw());
			put("pitch", location.getPitch());
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

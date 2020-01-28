package me.pugabyte.bncore.framework.persistence.serializer;

import com.dieselpoint.norm.serialize.DbSerializable;
import me.pugabyte.bncore.utils.SerializationUtils;
import org.bukkit.Location;

public class LocationSerializer implements DbSerializable {

	@Override
	public String serialize(Object in) {
		return SerializationUtils.serializeDatabaseLocation((Location) in);
	}

	@Override
	public Location deserialize(String in) {
		return SerializationUtils.deserializeDatabaseLocation(in);
	}

}

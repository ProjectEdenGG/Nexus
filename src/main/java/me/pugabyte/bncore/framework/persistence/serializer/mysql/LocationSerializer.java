package me.pugabyte.bncore.framework.persistence.serializer.mysql;

import com.dieselpoint.norm.serialize.DbSerializable;
import org.bukkit.Location;

import static me.pugabyte.bncore.utils.SerializationUtils.json_deserializeLocation;
import static me.pugabyte.bncore.utils.SerializationUtils.json_serializeLocation;

public class LocationSerializer implements DbSerializable {

	@Override
	public String serialize(Object in) {
		return json_serializeLocation((Location) in);
	}

	@Override
	public Location deserialize(String in) {
		return json_deserializeLocation(in);
	}

}

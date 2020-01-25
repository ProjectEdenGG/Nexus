package me.pugabyte.bncore.framework.persistence.serializer;

import com.dieselpoint.norm.serialize.DbSerializable;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

public class LocationSerializer implements DbSerializable {
	static DecimalFormat nf = new DecimalFormat("#.000");

	@Override
	public String serialize(Object in) {
		Location location = (Location) in;
		return location.getWorld().getName() + "," +
				nf.format(location.getX()) + "," +
				nf.format(location.getY()) + "," +
				nf.format(location.getZ()) + "," +
				nf.format(location.getYaw()) + "," +
				nf.format(location.getPitch());
	}

	@Override
	public Location deserialize(String in) {
		List<String> parts = Arrays.asList(in.split(","));
		return new Location(Bukkit.getWorld(parts.get(0)),
				Double.parseDouble(parts.get(1)),
				Double.parseDouble(parts.get(2)),
				Double.parseDouble(parts.get(3)),
				Float.parseFloat(parts.get(4)),
				Float.parseFloat(parts.get(5))
		);
	}
}

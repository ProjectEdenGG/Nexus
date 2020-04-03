package me.pugabyte.bncore.models.setting;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.bncore.framework.persistence.serializer.mysql.LocationSerializer;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Setting {
	@NonNull
	private String id;
	@NonNull
	private String type;
	private String value;

	public Setting(Player player, String type, String value) {
		this(player.getUniqueId().toString(), type, value);
	}

	public boolean getBoolean() {
		if ("1".equalsIgnoreCase(value)) return true;
		return Boolean.parseBoolean(value);
	}

	public void setBoolean(boolean value) {
		this.value = String.valueOf(value);
	}

	public Location getLocation() {
		return new LocationSerializer().deserialize(value);
	}

	public void setLocation(Location location) {
		this.value = new LocationSerializer().serialize(location);
	}

	public Map<String, Object> getJson() {
		Map<String, Object> map = new Gson().fromJson(value, Map.class);
		if (map == null)
			return new HashMap<>();
		return map;
	}

	public void setJson(Map<String, Object> map) {
		this.value = new Gson().toJson(new HashMap<>(map));
	}

}

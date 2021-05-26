package me.pugabyte.nexus.features.minigames.models.arenas;

import lombok.Data;
import lombok.ToString;
import me.pugabyte.nexus.features.minigames.models.Arena;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
@ToString(callSuper = true)
@SerializableAs("SabotageArena")
public class SabotageArena extends Arena {
	private Location meetingLocation;
	private int killCooldown = 25;
	private int meetingCooldown = 20;

	public SabotageArena(Map<String, Object> map) {
		super(map);
		meetingLocation = (Location) map.get("meetingLocation");
		killCooldown = (int) map.getOrDefault("killCooldown", killCooldown);
		meetingCooldown = (int) map.getOrDefault("meetingCooldown", meetingCooldown);
	}

	@Override
	public Map<String, Object> serialize() {
		LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) super.serialize();
		map.put("meetingLocation", meetingLocation);
		map.put("killCooldown", killCooldown);
		map.put("meetingCooldown", meetingCooldown);
		return map;
	}
}

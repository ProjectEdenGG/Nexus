package me.pugabyte.bncore.models.cooldown;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.framework.persistence.annotations.PlayerClass;
import me.pugabyte.bncore.models.MongoService;
import me.pugabyte.bncore.utils.Time;
import org.bukkit.OfflinePlayer;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static me.pugabyte.bncore.utils.StringUtils.timespanDiff;

@PlayerClass(Cooldown.class)
public class CooldownService extends MongoService {
	private final static Map<UUID, Cooldown> cache = new HashMap<>();

	public Map<UUID, Cooldown> getCache() {
		return cache;
	}

	public boolean check(OfflinePlayer player, String type, Time time) {
		return check(player.getUniqueId(), type, time);
	}

	public boolean check(UUID uuid, String type, Time time) {
		return check(uuid, type, time.get());
	}

	public boolean check(OfflinePlayer player, String type, double ticks) {
		return check(player.getUniqueId(), type, ticks);
	}

	public boolean check(UUID uuid, String type, double ticks) {
		Cooldown cooldown = get(uuid);
		if (cooldown == null) {
			BNCore.warn("Cooldown object is null? " + uuid.toString() + " / " + type + " / " + ticks);
			return false;
		}

		if (!cooldown.check(type))
			return false;

		cooldown = cooldown.create(type, ticks);
		save(cooldown);
		return true;
	}

	public String getDiff(OfflinePlayer player, String type) {
		return getDiff(player.getUniqueId(), type);
	}

	public String getDiff(UUID uuid, String type) {
		Cooldown cooldown = get(uuid);
		if (cooldown.exists(type))
			return timespanDiff(LocalDateTime.now(), cooldown.get(type));

		return ".0s";
	}

}

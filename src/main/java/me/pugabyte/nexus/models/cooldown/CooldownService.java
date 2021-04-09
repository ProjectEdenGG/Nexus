package me.pugabyte.nexus.models.cooldown;

import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.framework.persistence.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.Time;
import org.bukkit.OfflinePlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static me.pugabyte.nexus.utils.StringUtils.timespanDiff;

/*
	Returns true, if cooldown has expired/is bypassed
	Returns false, if cooldown is still in effect
 */
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

	public boolean check(UUID uuid, String type, double ticks, String bypassPermission) {
		OfflinePlayer player = PlayerUtils.getPlayer(uuid);
		if (player.isOnline() && player.getPlayer() != null && player.getPlayer().hasPermission(bypassPermission))
			return true;

		return check(uuid, type, ticks);
	}

	public boolean check(UUID uuid, String type, double ticks) {
		Cooldown cooldown = get(uuid);
		if (cooldown == null) {
			Nexus.warn("Cooldown object is null? " + uuid.toString() + " / " + type + " / " + ticks);
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
			return timespanDiff(cooldown.get(type));

		return ".0s";
	}

}

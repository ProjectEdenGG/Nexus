package me.pugabyte.nexus.models.cooldown;

import eden.mongodb.annotations.PlayerClass;
import eden.utils.TimeUtils.Time;
import eden.utils.TimeUtils.Timespan;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.models.MongoService;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.OfflinePlayer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

import static eden.utils.Utils.isNullOrEmpty;

/*
	Returns true, if cooldown has expired/is bypassed
	Returns false, if cooldown is still in effect
 */
@PlayerClass(Cooldown.class)
public class CooldownService extends MongoService<Cooldown> {

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
			return Timespan.of(cooldown.get(type)).format();

		return ".0s";
	}

	static {
		Tasks.repeatAsync(Time.MINUTE, Time.HOUR, () -> new CooldownService().janitor());
	}

	public int janitor() {
		int count = 0;
		for (Object object : getAll()) {
			Cooldown cooldown = get((Cooldown) object);
			for (String key : new HashSet<>(cooldown.getCooldowns().keySet()))
				if (cooldown.check(key)) {
					cooldown.getCooldowns().remove(key);
					++count;
				}
		}

		saveCacheSync();
		return count;
	}

	@Override
	protected boolean deleteIf(Cooldown cooldown) {
		return isNullOrEmpty(cooldown.getCooldowns());
	}

	@Override
	protected void beforeSave(Cooldown cooldown) {
		for (String key : new HashSet<>(cooldown.getCooldowns().keySet()))
			if (cooldown.check(key))
				cooldown.getCooldowns().remove(key);
	}

}

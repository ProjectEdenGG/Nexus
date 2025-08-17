package gg.projecteden.nexus.models.cooldown;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.api.common.utils.TimeUtils.Timespan;
import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.OfflinePlayer;

import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(Cooldown.class)
public class CooldownService extends MongoPlayerService<Cooldown> {
	private final static Map<UUID, Cooldown> cache = new ConcurrentHashMap<>();

	public Map<UUID, Cooldown> getCache() {
		return cache;
	}

	public static boolean isOnCooldown(OfflinePlayer player, String type, TickTime time) {
		return isOnCooldown(player.getUniqueId(), type, time);
	}

	public static boolean isOnCooldown(UUID uuid, String type, TickTime time) {
		return isOnCooldown(uuid, type, time.get());
	}

	public static boolean isOnCooldown(OfflinePlayer player, String type, long ticks) {
		return isOnCooldown(player.getUniqueId(), type, ticks);
	}

	public static boolean isOnCooldown(UUID uuid, String type, long ticks, String bypassPermission) {
		OfflinePlayer player = PlayerUtils.getPlayer(uuid);
		if (player.getPlayer() != null && player.getPlayer().hasPermission(bypassPermission))
			return true;

		return isOnCooldown(uuid, type, ticks);
	}

	public static boolean isOnCooldown(UUID uuid, String type, long ticks) {
		return isOnCooldown(uuid, type, ticks, true);
	}

	public static boolean isOnCooldown(UUID uuid, String type, long ticks, boolean createIfTrue) {
		return new CooldownService().isOnCooldown0(uuid, type, ticks, createIfTrue);
	}

	public static boolean isNotOnCooldown(OfflinePlayer player, String type, TickTime time) {
		return !isOnCooldown(player, type, time);
	}

	public static boolean isNotOnCooldown(UUID uuid, String type, TickTime time) {
		return !isOnCooldown(uuid, type, time);
	}

	public static boolean isNotOnCooldown(OfflinePlayer player, String type, long ticks) {
		return !isOnCooldown(player, type, ticks);
	}

	public static boolean isNotOnCooldown(UUID uuid, String type, long ticks, String bypassPermission) {
		return !isOnCooldown(uuid, type, ticks, bypassPermission);
	}

	public static boolean isNotOnCooldown(UUID uuid, String type, long ticks) {
		return !isOnCooldown(uuid, type, ticks);
	}

	public static boolean isNotOnCooldown(UUID uuid, String type, long ticks, boolean createIfTrue) {
		return !isOnCooldown(uuid, type, ticks, createIfTrue);
	}

	private boolean isOnCooldown0(UUID uuid, String type, long ticks, boolean createIfTrue) {
		if (ticks == 0)
			return false;

		Cooldown cooldown = get(uuid);
		if (cooldown == null) {
			Nexus.warn("Cooldown object is null? " + uuid.toString() + " / " + type + " / " + ticks);
			return true;
		}

		if (cooldown.isOnCooldown(type))
			return true;

		if (createIfTrue) {
			cooldown = cooldown.create(type, ticks);
			save(cooldown);
		}
		return false;
	}

	/**
	 * Gets a human-readable string for the time left on a player's cooldown.
	 * @param player player to check (or Nexus.UUID0)
	 * @param type an arbitrary string corresponding to the type of cooldown matching the regex ^[\w:#-]+$
	 * @return a human-readable string per {@link Timespan#format()}
	 */
	public String getDiff(OfflinePlayer player, String type) {
		return getDiff(player.getUniqueId(), type);
	}

	/**
	 * Gets a human-readable string for the time left on a player's cooldown.
	 * @param uuid player UUID to check (or Nexus.UUID0)
	 * @param type an arbitrary string corresponding to the type of cooldown matching the regex ^[\w:#-]+$
	 * @return a human-readable string per {@link Timespan#format()}
	 */
	public String getDiff(UUID uuid, String type) {
		Cooldown cooldown = get(uuid);
		if (cooldown.exists(type))
			return Timespan.of(cooldown.get(type)).format();

		return ".0s";
	}

	static {
		Tasks.repeatAsync(TickTime.MINUTE, TickTime.HOUR, () -> new CooldownService().janitor());
	}

	public int janitor() {
		int count = 0;
		for (Cooldown cooldown : getAll()) {
			for (String key : new HashSet<>(cooldown.getCooldowns().keySet()))
				if (cooldown.isNotOnCooldown(key)) {
					cooldown.getCooldowns().remove(key);
					++count;
				}
		}

		saveCache();
		return count;
	}

	@Override
	protected boolean deleteIf(Cooldown cooldown) {
		return Nullables.isNullOrEmpty(cooldown.getCooldowns());
	}

	@Override
	protected void beforeSave(Cooldown cooldown) {
		for (String key : new HashSet<>(cooldown.getCooldowns().keySet()))
			try {
				if (cooldown.isNotOnCooldown(key))
					cooldown.getCooldowns().remove(key);
			} catch (NullPointerException ignore) {}
	}

}

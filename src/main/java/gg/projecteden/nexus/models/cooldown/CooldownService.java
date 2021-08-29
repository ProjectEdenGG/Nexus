package gg.projecteden.nexus.models.cooldown;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.models.MongoService;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.TimeUtils.TickTime;
import gg.projecteden.utils.TimeUtils.Timespan;
import org.bukkit.OfflinePlayer;

import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static gg.projecteden.utils.Utils.isNullOrEmpty;

@PlayerClass(Cooldown.class)
public class CooldownService extends MongoService<Cooldown> {
	private final static Map<UUID, Cooldown> cache = new ConcurrentHashMap<>();

	public Map<UUID, Cooldown> getCache() {
		return cache;
	}

	/**
	 * Checks if a player is currently off cooldown.
	 * <p>
	 * Returns true if the player is not on cooldown. This will also create a new cooldown instance.
	 * @param player player to check
	 * @param type an arbitrary string corresponding to the type of cooldown matching the regex ^[\w:#-]+$
	 * @param time how long the cooldown should last
	 * @return true if player is not on cooldown
	 */
	public boolean check(OfflinePlayer player, String type, TickTime time) {
		return check(player.getUniqueId(), type, time);
	}

	/**
	 * Checks if a player is currently off cooldown.
	 * <p>
	 * Returns true if the player is not on cooldown. This will also create a new cooldown instance.
	 * @param uuid player UUID to check (or Nexus.UUID0)
	 * @param type an arbitrary string corresponding to the type of cooldown matching the regex ^[\w:#-]+$
	 * @param time how long the cooldown should last
	 * @return true if player is not on cooldown
	 */
	public boolean check(UUID uuid, String type, TickTime time) {
		return check(uuid, type, time.get());
	}

	/**
	 * Checks if a player is currently off cooldown.
	 * <p>
	 * Returns true if the player is not on cooldown. This will also create a new cooldown instance.
	 * @param player player to check
	 * @param type an arbitrary string corresponding to the type of cooldown matching the regex ^[\w:#-]+$
	 * @param ticks how long the cooldown should last in ticks
	 * @return true if player is not on cooldown
	 */
	public boolean check(OfflinePlayer player, String type, double ticks) {
		return check(player.getUniqueId(), type, ticks);
	}

	/**
	 * Checks if a player is currently off cooldown.
	 * <p>
	 * Returns true if the player is not on cooldown or if the player bypasses this cooldown. This will also create a new cooldown instance.
	 * @param uuid player UUID to check (or Nexus.UUID0)
	 * @param type an arbitrary string corresponding to the type of cooldown matching the regex ^[\w:#-]+$
	 * @param ticks how long the cooldown should last in ticks
	 * @param bypassPermission permission the player must have to bypass this cooldown
	 * @return true if player is not on cooldown
	 */
	public boolean check(UUID uuid, String type, double ticks, String bypassPermission) {
		OfflinePlayer player = PlayerUtils.getPlayer(uuid);
		if (player.getPlayer() != null && player.getPlayer().hasPermission(bypassPermission))
			return true;

		return check(uuid, type, ticks);
	}

	/**
	 * Checks if a player is currently off cooldown.
	 * <p>
	 * Returns true if the player is not on cooldown. This will also create a new cooldown instance.
	 * @param uuid player UUID to check (or Nexus.UUID0)
	 * @param type an arbitrary string corresponding to the type of cooldown matching the regex ^[\w:#-]+$
	 * @param ticks how long the cooldown should last in ticks
	 * @return true if player is not on cooldown
	 */
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

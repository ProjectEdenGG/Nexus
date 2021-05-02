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

@PlayerClass(Cooldown.class)
public class CooldownService extends MongoService<Cooldown> {
	private final static Map<UUID, Cooldown> cache = new HashMap<>();
	private static final Map<UUID, Integer> saveQueue = new HashMap<>();

	public Map<UUID, Cooldown> getCache() {
		return cache;
	}

	protected Map<UUID, Integer> getSaveQueue() {
		return saveQueue;
	}

	/**
	 * Checks if a player is currently off cooldown.
	 * <p>
	 * Returns true if the player is not on cooldown. This will also create a new cooldown instance.
	 * @param player player to check
	 * @param type an arbitrary string corresponding to the type of cooldown matching the regex ^[A-Za-z_-]+$
	 * @param time how long the cooldown should last
	 * @return true if player is not on cooldown
	 */
	public boolean check(OfflinePlayer player, String type, Time time) {
		return check(player.getUniqueId(), type, time);
	}

	/**
	 * Checks if a player is currently off cooldown.
	 * <p>
	 * Returns true if the player is not on cooldown. This will also create a new cooldown instance.
	 * @param uuid player UUID to check (or Nexus.UUID0)
	 * @param type an arbitrary string corresponding to the type of cooldown matching the regex ^[A-Za-z_-]+$
	 * @param time how long the cooldown should last
	 * @return true if player is not on cooldown
	 */
	public boolean check(UUID uuid, String type, Time time) {
		return check(uuid, type, time.get());
	}

	/**
	 * Checks if a player is currently off cooldown.
	 * <p>
	 * Returns true if the player is not on cooldown. This will also create a new cooldown instance.
	 * @param player player to check
	 * @param type an arbitrary string corresponding to the type of cooldown matching the regex ^[A-Za-z_-]+$
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
	 * @param type an arbitrary string corresponding to the type of cooldown matching the regex ^[A-Za-z_-]+$
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
	 * @param type an arbitrary string corresponding to the type of cooldown matching the regex ^[A-Za-z_-]+$
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
	 * @param type an arbitrary string corresponding to the type of cooldown matching the regex ^[A-Za-z_-]+$
	 * @return a human-readable string per {@link Timespan#format()}
	 */
	public String getDiff(OfflinePlayer player, String type) {
		return getDiff(player.getUniqueId(), type);
	}

	/**
	 * Gets a human-readable string for the time left on a player's cooldown.
	 * @param uuid player UUID to check (or Nexus.UUID0)
	 * @param type an arbitrary string corresponding to the type of cooldown matching the regex ^[A-Za-z_-]+$
	 * @return a human-readable string per {@link Timespan#format()}
	 */
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

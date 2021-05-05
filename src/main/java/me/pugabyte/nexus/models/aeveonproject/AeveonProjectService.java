package me.pugabyte.nexus.models.aeveonproject;

import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;
import org.bukkit.OfflinePlayer;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(AeveonProjectUser.class)
public class AeveonProjectService extends MongoService<AeveonProjectUser> {
	private final static Map<UUID, AeveonProjectUser> cache = new ConcurrentHashMap<>();
	private static final Map<UUID, Integer> saveQueue = new ConcurrentHashMap<>();

	public Map<UUID, AeveonProjectUser> getCache() {
		return cache;
	}

	protected Map<UUID, Integer> getSaveQueue() {
		return saveQueue;
	}

	public boolean hasStarted(OfflinePlayer player) {
		List<AeveonProjectUser> users = getAll();
		for (AeveonProjectUser user : users) {
			if (user.getUuid().equals(player.getUniqueId()))
				return true;
		}
		return false;
	}

}

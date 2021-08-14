package gg.projecteden.nexus.models.aeveonproject;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;
import org.bukkit.OfflinePlayer;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(AeveonProjectUser.class)
public class AeveonProjectService extends MongoService<AeveonProjectUser> {
	private final static Map<UUID, AeveonProjectUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, AeveonProjectUser> getCache() {
		return cache;
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

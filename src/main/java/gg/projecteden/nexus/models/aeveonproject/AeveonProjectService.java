package gg.projecteden.nexus.models.aeveonproject;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;
import org.bukkit.OfflinePlayer;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(AeveonProjectUser.class)
public class AeveonProjectService extends MongoPlayerService<AeveonProjectUser> {
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

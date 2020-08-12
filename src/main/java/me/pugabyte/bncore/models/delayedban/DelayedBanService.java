package me.pugabyte.bncore.models.delayedban;

import me.pugabyte.bncore.framework.persistence.annotations.PlayerClass;
import me.pugabyte.bncore.models.MongoService;
import org.bukkit.OfflinePlayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@PlayerClass(DelayedBan.class)
public class DelayedBanService extends MongoService {
	private final static Map<UUID, DelayedBan> cache = new HashMap<>();

	@Override
	public Map<UUID, DelayedBan> getCache() {
		return cache;
	}

	public boolean hasQueuedBan(OfflinePlayer player) {
		List<DelayedBan> delayedBans = getAll();
		for (DelayedBan delayedBan : delayedBans) {
			if (delayedBan.getUuid().equals(player.getUniqueId()))
				return true;
		}
		return false;
	}
}

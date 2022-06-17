package gg.projecteden.nexus.models.spawnlimits;

import gg.projecteden.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(SpawnLimits.class)
public class SpawnLimitsService extends MongoPlayerService<SpawnLimits> {
	private final static Map<UUID, SpawnLimits> cache = new ConcurrentHashMap<>();

	public Map<UUID, SpawnLimits> getCache() {
		return cache;
	}

	@Override
	protected void beforeSave(SpawnLimits limits) {
		var settings = new HashMap<>(limits.getSettings());
		for (World world : settings.keySet())
			if (settings.get(world).isEmpty())
				limits.getSettings().remove(world);
	}

}

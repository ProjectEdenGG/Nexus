package gg.projecteden.nexus.models.spawnlimits;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(SpawnLimits.class)
public class SpawnLimitsService extends MongoService<SpawnLimits> {
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

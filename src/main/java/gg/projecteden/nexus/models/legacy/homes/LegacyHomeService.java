package gg.projecteden.nexus.models.legacy.homes;


import gg.projecteden.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(LegacyHomeOwner.class)
public class LegacyHomeService extends MongoPlayerService<LegacyHomeOwner> {
	private final static Map<UUID, LegacyHomeOwner> cache = new ConcurrentHashMap<>();

	public Map<UUID, LegacyHomeOwner> getCache() {
		return cache;
	}

	@Override
	@NotNull
	public LegacyHomeOwner get(UUID uuid) {
		LegacyHomeOwner homeOwner = super.get(uuid);
		homeOwner.getHomes().sort(Comparator.comparing(home -> home.getName().toLowerCase()));
		return homeOwner;
	}

	@Override
	public void beforeSave(LegacyHomeOwner homeOwner) {
		homeOwner.getHomes().sort(Comparator.comparing(home -> home.getName().toLowerCase()));
	}

}

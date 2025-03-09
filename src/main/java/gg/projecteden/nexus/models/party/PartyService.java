package gg.projecteden.nexus.models.party;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoBukkitService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(PartyManager.class)
public class PartyService extends MongoBukkitService<PartyManager> {

	private final static Map<UUID, PartyManager> cache = new ConcurrentHashMap<>();

	@Override
	public Map<UUID, PartyManager> getCache() {
		return cache;
	}

	public void save() {
		save(get0());
	}

}

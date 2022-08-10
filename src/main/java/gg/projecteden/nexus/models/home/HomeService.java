package gg.projecteden.nexus.models.home;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;

import java.util.Comparator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(HomeOwner.class)
public class HomeService extends MongoPlayerService<HomeOwner> {
	private final static Map<UUID, HomeOwner> cache = new ConcurrentHashMap<>();

	public Map<UUID, HomeOwner> getCache() {
		return cache;
	}

	@Override
	public void beforeSave(HomeOwner homeOwner) {
		homeOwner.getHomes().sort(Comparator.comparing(home -> home.getName().toLowerCase()));
	}

}

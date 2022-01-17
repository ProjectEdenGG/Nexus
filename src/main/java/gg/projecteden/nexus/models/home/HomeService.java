package gg.projecteden.nexus.models.home;


import gg.projecteden.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;
import org.jetbrains.annotations.NotNull;

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
	@NotNull
	public HomeOwner get(UUID uuid) {
		HomeOwner homeOwner = super.get(uuid);
		homeOwner.getHomes().sort(Comparator.comparing(home -> home.getName().toLowerCase()));
		return homeOwner;
	}

	@Override
	public void beforeSave(HomeOwner homeOwner) {
		homeOwner.getHomes().sort(Comparator.comparing(home -> home.getName().toLowerCase()));
	}

}

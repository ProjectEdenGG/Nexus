package gg.projecteden.nexus.models.home;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(HomeOwner.class)
public class HomeService extends MongoService<HomeOwner> {
	private final static Map<UUID, HomeOwner> cache = new ConcurrentHashMap<>();
	private static final Map<UUID, Integer> saveQueue = new ConcurrentHashMap<>();

	public Map<UUID, HomeOwner> getCache() {
		return cache;
	}

	protected Map<UUID, Integer> getSaveQueue() {
		return saveQueue;
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

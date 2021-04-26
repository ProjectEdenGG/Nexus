package me.pugabyte.nexus.models.home;

import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(HomeOwner.class)
public class HomeService extends MongoService<HomeOwner> {
	private final static Map<UUID, HomeOwner> cache = new HashMap<>();

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
	public void save(HomeOwner homeOwner) {
		homeOwner.getHomes().sort(Comparator.comparing(home -> home.getName().toLowerCase()));
		super.save(homeOwner);
	}

}

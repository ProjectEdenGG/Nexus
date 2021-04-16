package me.pugabyte.nexus.models.radio;

import me.pugabyte.nexus.framework.persistence.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(RadioUser.class)
public class RadioUserService extends MongoService<RadioUser> {

	public static Map<UUID, RadioUser> cache = new HashMap<>();

	@Override
	public Map<UUID, RadioUser> getCache() {
		return cache;
	}


}

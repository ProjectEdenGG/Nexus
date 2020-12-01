package me.pugabyte.nexus.models.pugmas20;

import me.pugabyte.nexus.framework.persistence.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import me.pugabyte.nexus.utils.Tasks;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static me.pugabyte.nexus.utils.StringUtils.isV4Uuid;

@PlayerClass(Pugmas20User.class)
public class Pugmas20Service extends MongoService {

	public static Map<UUID, Pugmas20User> cache = new HashMap<>();

	@Override
	public Map<UUID, Pugmas20User> getCache() {
		return cache;
	}

	@Override
	public <T> void save(T object) {
		PlayerOwnedObject playerOwnedObject = (PlayerOwnedObject) object;
		log("Save " + playerOwnedObject.getName() + " [" + playerOwnedObject.getUuid().toString() + "]");
		Tasks.async(() -> {
			super.deleteSync(object);
			super.saveSync(object);
		});
	}

	@Override
	public <T> void deleteSync(T object) {
		PlayerOwnedObject playerOwnedObject = (PlayerOwnedObject) object;
		log("DeleteSync " + playerOwnedObject.getName() + " [" + playerOwnedObject.getUuid().toString() + "]");

		if (!isV4Uuid(playerOwnedObject.getUuid()))
			return;

		database.delete(object);
	}

}

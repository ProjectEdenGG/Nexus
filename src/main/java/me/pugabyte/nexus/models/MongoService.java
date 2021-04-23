package me.pugabyte.nexus.models;

import me.pugabyte.nexus.framework.persistence.annotations.PlayerClass;
import org.bukkit.OfflinePlayer;

public abstract class MongoService<T extends PlayerOwnedObject> extends eden.mongodb.MongoService<T> {

	public T get(OfflinePlayer player) {
		return get(player.getUniqueId());
	}

	@Override
	public Class<T> getPlayerClass() {
		PlayerClass annotation = getClass().getAnnotation(PlayerClass.class);
		return annotation == null ? null : (Class<T>) annotation.value();
	}

}

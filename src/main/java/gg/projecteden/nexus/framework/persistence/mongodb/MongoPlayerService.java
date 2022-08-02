package gg.projecteden.nexus.framework.persistence.mongodb;

import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class MongoPlayerService<T extends PlayerOwnedObject> extends MongoBukkitService<T> {

	@Override
	protected String pretty(T object) {
		return object.getNickname();
	}

	public List<T> getOnline() {
		List<T> online = new ArrayList<>();
		for (Player player : OnlinePlayers.getAll())
			online.add(get(player));
		return online;
	}

	public void saveOnline() {
		for (T user : getOnline())
			save(user);
	}

	public void saveOnlineSync() {
		for (T user : getOnline())
			saveSync(user);
	}

}

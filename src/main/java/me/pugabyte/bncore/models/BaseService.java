package me.pugabyte.bncore.models;

import com.dieselpoint.norm.Database;
import me.pugabyte.bncore.framework.persistence.BearNationDatabase;
import me.pugabyte.bncore.framework.persistence.Persistence;
import me.pugabyte.bncore.models.nerds.Nerd;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class BaseService {
	protected Database database = Persistence.getConnection(BearNationDatabase.BEARNATION);

	public Object get(String uuid) {
		return null;
	}

	public Object get(UUID uuid) {
		return get(uuid.toString());
	}

	public Object get(Player player) {
		return get(player.getUniqueId());
	}

	public Object get(OfflinePlayer player) {
		return get(player.getUniqueId());
	}

	public Object get(Nerd nerd) {
		return get(nerd.getOfflinePlayer());
	}

	protected String asList(List<String> list) {
		return "\"" + String.join("\",\"", list) + "\"";
	}
}

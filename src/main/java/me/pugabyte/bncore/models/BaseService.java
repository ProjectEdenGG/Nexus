package me.pugabyte.bncore.models;

import com.dieselpoint.norm.Database;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.framework.persistence.BearNationDatabase;
import me.pugabyte.bncore.framework.persistence.Persistence;
import me.pugabyte.bncore.models.nerds.Nerd;
import me.pugabyte.bncore.utils.Tasks;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

public abstract class BaseService {
	protected static Database database = Persistence.getConnection(BearNationDatabase.BEARNATION);

	public <T> T get(String uuid) {
		return null;
	}

	public <T> T get(UUID uuid) {
		return (T) get(uuid.toString());
	}

	public <T> T get(Player player) {
		return (T) get(player.getUniqueId());
	}

	public <T> T get(OfflinePlayer player) {
		return (T) get(player.getUniqueId());
	}

	public <T> T get(Nerd nerd) {
		return (T) get(nerd.getOfflinePlayer());
	}

	public <T> void save(T object) {
		Tasks.async(() -> database.upsert(object).execute());
	}

	protected String asList(List<String> list) {
		return "\"" + String.join("\",\"", list) + "\"";
	}

	public String safe(String input) {
		if (Pattern.compile("[\\w\\d\\s]+").matcher(input).matches())
			return input;
		throw new InvalidInputException("Unsafe argument");
	}
}

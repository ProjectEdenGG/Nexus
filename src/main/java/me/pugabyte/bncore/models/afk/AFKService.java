package me.pugabyte.bncore.models.afk;

import me.pugabyte.bncore.features.afk.AFK;
import me.pugabyte.bncore.models.BaseService;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AFKService extends BaseService {

	public void saveAll() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			AFKPlayer afkPlayer = AFK.get(player);
			if (afkPlayer.isAfk())
				database.upsert(afkPlayer);
		}
	}

	public Map<Player, AFKPlayer> getAll() {
		try {
			List<AFKPlayer> results = database.where("uuid in (" + asList(Utils.getOnlineUuids()) + ")").results(AFKPlayer.class);
			database.table("afk").delete();
			Map<Player, AFKPlayer> players = new HashMap<>();
			for (AFKPlayer afkPlayer : results) {
				Player player = Bukkit.getPlayer(UUID.fromString(afkPlayer.getUuid()));
				afkPlayer.setPlayer(player);
				players.put(player, afkPlayer);
			}
			return players;
		} catch (Exception ex) {
			ex.printStackTrace();
			return new HashMap<>();
		}
	}

}

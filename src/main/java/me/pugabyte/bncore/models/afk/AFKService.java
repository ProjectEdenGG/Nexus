package me.pugabyte.bncore.models.afk;

import me.pugabyte.bncore.features.afk.AFK;
import me.pugabyte.bncore.models.MySQLService;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AFKService extends MySQLService {

	public void saveAll() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			AFKPlayer afkPlayer = AFK.get(player);
			if (afkPlayer.isAfk())
				database.upsert(afkPlayer);
		}
	}

	public Map<Player, AFKPlayer> getMap() {
		try {
			List<AFKPlayer> results = database.where("uuid in (" + asList(Utils.getOnlineUuids()) + ")").results(AFKPlayer.class);
			Tasks.async(() -> database.table("afk").delete());
			Map<Player, AFKPlayer> players = new HashMap<>();
			for (AFKPlayer afkPlayer : results) {
				OfflinePlayer player = Utils.getPlayer(afkPlayer.getUuid());
				if (player.isOnline()) {
					afkPlayer.setPlayer(player.getPlayer());
					players.put(player.getPlayer(), afkPlayer);
				}
			}
			return players;
		} catch (Exception ex) {
			ex.printStackTrace();
			return new HashMap<>();
		}
	}

}

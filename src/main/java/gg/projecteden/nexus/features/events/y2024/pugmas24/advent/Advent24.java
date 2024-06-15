package gg.projecteden.nexus.features.events.y2024.pugmas24.advent;

import gg.projecteden.nexus.features.events.y2021.pugmas21.Pugmas21;
import gg.projecteden.nexus.models.pugmas24.Advent24Config;
import gg.projecteden.nexus.models.pugmas24.Advent24Present;
import gg.projecteden.nexus.models.pugmas24.Pugmas24User;
import gg.projecteden.nexus.models.pugmas24.Pugmas24UserService;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import org.bukkit.entity.Player;

// TODO
public class Advent24 {

	private static final Pugmas24UserService userService = new Pugmas24UserService();

	public static void shutdown() {
		for (Player player : OnlinePlayers.where().world(Pugmas21.getWorld()).get()) {
			final Pugmas24User user = userService.get(player);
			for (Advent24Present present : Advent24Config.get().getPresents())
				user.advent().hide(present);
		}
	}

	public static void glow(Pugmas24User user, int day) {
		// TODO
		user.sendMessage("TODO: day = " + day);
	}
}

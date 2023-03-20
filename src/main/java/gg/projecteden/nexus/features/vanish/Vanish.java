package gg.projecteden.nexus.features.vanish;

import gg.projecteden.nexus.features.vanish.events.PreUnvanishEvent;
import gg.projecteden.nexus.features.vanish.events.PreVanishEvent;
import gg.projecteden.nexus.features.vanish.events.UnvanishEvent;
import gg.projecteden.nexus.features.vanish.events.VanishEvent;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.models.vanish.VanishUser;
import gg.projecteden.nexus.models.vanish.VanishUser.Priority;
import gg.projecteden.nexus.models.vanish.VanishUserService;
import gg.projecteden.nexus.utils.PlayerUtils.HidePlayer;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.PlayerUtils.ShowPlayer;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.parchment.OptionalPlayer;
import org.bukkit.entity.Player;

public class Vanish extends Feature {
	public static final String PREFIX = StringUtils.getPrefix(Vanish.class);
	private static final VanishUserService service = new VanishUserService();

	@Override
	public void onStart() {
		new VanishListener();
	}

	public static VanishUser get(Player player) {
		return service.get(player);
	}

	public static boolean vanish(Player player) {
		return vanish(player, Priority.STAFF);
	}

	public static boolean vanish(Player player, Priority priority) {
		if (get(player).isVanished())
			return true;

		if (!new PreVanishEvent(player).callEvent())
			return false;

		service.edit(player, user -> {
			user.setVanished(true);
			user.setPriority(priority);

			new HidePlayer(player).from(OnlinePlayers.where(user::canHideFrom).get());
		});

		return new VanishEvent(player).callEvent();
	}

	public static boolean unvanish(Player player) {
		if (!get(player).isVanished())
			return true;

		if (!new PreUnvanishEvent(player).callEvent())
			return false;

		service.edit(player, user -> {
			user.setVanished(false);
			user.setPriority(null);

			new ShowPlayer(player).toAll();
		});

		return new UnvanishEvent(player).callEvent();
	}

	public static boolean isVanished(OptionalPlayer player) {
		if (player.getPlayer() == null)
			return false;

		return get(player.getPlayer()).isVanished();
	}

}

package gg.projecteden.nexus.features.vanish;

import gg.projecteden.api.interfaces.HasUniqueId;
import gg.projecteden.nexus.features.vanish.events.PreVanishToggleEvent;
import gg.projecteden.nexus.features.vanish.events.VanishToggleEvent;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.models.vanish.VanishUser;
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

	public static VanishUser get(HasUniqueId player) {
		return service.get(player);
	}

	public static boolean vanish(Player player) {
		if (get(player).isVanished())
			return true;

		if (!new PreVanishToggleEvent(player).callEvent())
			return false;

		service.edit(player, user -> user.setVanished(true));

		refresh(player);

		return new VanishToggleEvent(player).callEvent();
	}

	public static boolean unvanish(Player player) {
		if (!get(player).isVanished())
			return true;

		if (!new PreVanishToggleEvent(player).callEvent())
			return false;

		service.edit(player, user -> user.setVanished(false));

		refresh(player);

		return new VanishToggleEvent(player).callEvent();
	}

	public static void refreshAll() {
		OnlinePlayers.getAll().forEach(Vanish::refresh);
	}

	public static void refresh(Player player) {
		refresh(get(player));
	}

	public static void refresh(VanishUser user) {
		if (user.isVanished())
			new HidePlayer(user).from(OnlinePlayers.where(user::canHideFrom).get());
		else
			new ShowPlayer(user).toAll();
	}

	public static boolean isVanished(OptionalPlayer player) {
		if (player.getPlayer() == null)
			return false;

		return get(player.getPlayer()).isVanished();
	}

}

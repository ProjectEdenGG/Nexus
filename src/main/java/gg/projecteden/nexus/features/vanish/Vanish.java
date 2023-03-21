package gg.projecteden.nexus.features.vanish;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.api.interfaces.HasUniqueId;
import gg.projecteden.nexus.features.vanish.events.PreVanishToggleEvent;
import gg.projecteden.nexus.features.vanish.events.VanishToggleEvent;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.vanish.VanishUser;
import gg.projecteden.nexus.models.vanish.VanishUser.Setting;
import gg.projecteden.nexus.models.vanish.VanishUserService;
import gg.projecteden.nexus.utils.ActionBarUtils;
import gg.projecteden.nexus.utils.PlayerUtils.HidePlayer;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.PlayerUtils.ShowPlayer;
import gg.projecteden.nexus.utils.PotionEffectBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.parchment.OptionalPlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class Vanish extends Feature {
	public static final String PREFIX = StringUtils.getPrefix(Vanish.class);
	private static final VanishUserService service = new VanishUserService();
	private static final PotionEffectBuilder NIGHT_VISION = new PotionEffectBuilder(PotionEffectType.NIGHT_VISION).maxDuration();

	@Override
	public void onStart() {
		new VanishListener();

		Tasks.repeat(0, TickTime.SECOND.x(2), () ->
			OnlinePlayers.getAll().stream()
				.map(Vanish::get)
				.filter(VanishUser::isVanished)
				.forEach(user -> {
					ActionBarUtils.sendActionBar(user.getOnlinePlayer(), "&3You are vanished!");

					if (user.getSetting(Setting.NIGHT_VISION))
						user.getOnlinePlayer().addPotionEffect(NIGHT_VISION.build());
				}));
	}

	public static VanishUser get(HasUniqueId player) {
		return service.get(player);
	}

	public static boolean vanish(Player player) {
		if (get(player).isVanished())
			return true;

		if (!new PreVanishToggleEvent(player).callEvent())
			return false;

		service.edit(player, user -> {
			user.setVanished(true);

			if (user.getSetting(Setting.NIGHT_VISION))
				player.addPotionEffect(NIGHT_VISION.build());
		});

		refresh(player);

		return new VanishToggleEvent(player).callEvent();
	}

	public static boolean unvanish(Player player) {
		if (!get(player).isVanished())
			return true;

		if (!new PreVanishToggleEvent(player).callEvent())
			return false;

		service.edit(player, user -> {
			user.setVanished(false);

			if (player.hasPotionEffect(PotionEffectType.NIGHT_VISION))
				if (user.getSetting(Setting.NIGHT_VISION) && !Nerd.of(user).isNightVision())
					player.removePotionEffect(PotionEffectType.NIGHT_VISION);
		});

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

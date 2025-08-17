package gg.projecteden.nexus.features.vanish;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.api.interfaces.HasUniqueId;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.commands.FlyCommand;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.vanish.events.PreVanishToggleEvent;
import gg.projecteden.nexus.features.vanish.events.VanishToggleEvent;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.vanish.VanishUser;
import gg.projecteden.nexus.models.vanish.VanishUser.Setting;
import gg.projecteden.nexus.models.vanish.VanishUserService;
import gg.projecteden.nexus.utils.ActionBarUtils;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.PotionEffectBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.parchment.OptionalPlayer;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffectType;

import static gg.projecteden.nexus.utils.PlayerUtils.hidePlayer;
import static gg.projecteden.nexus.utils.PlayerUtils.showPlayer;

public class Vanish extends Feature {
	public static final String PREFIX = StringUtils.getPrefix(Vanish.class);
	private static final VanishUserService service = new VanishUserService();
	private static final PotionEffectBuilder NIGHT_VISION = new PotionEffectBuilder(PotionEffectType.NIGHT_VISION).infinite();

	@Override
	public void onStart() {
		new VanishListener();

		Tasks.repeat(0, TickTime.TICK, Vanish::refreshAll);

		Tasks.repeat(0, TickTime.SECOND.x(2), () -> {
			OnlinePlayers.getAll().stream()
				.map(Vanish::get)
				.filter(VanishUser::isVanished)
				.forEach(user -> {
					ActionBarUtils.sendActionBar(user.getOnlinePlayer(), "&3You are vanished!");

					if (user.getSetting(Setting.NIGHT_VISION))
						user.getOnlinePlayer().addPotionEffect(NIGHT_VISION.build());
				});
		});
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

			player.setMetadata("vanished", new FixedMetadataValue(Nexus.getInstance(), true));

			if (user.getSetting(Setting.NIGHT_VISION))
				player.addPotionEffect(NIGHT_VISION.build());
		});

		FlyCommand.on(player, Vanish.class);

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

			player.removeMetadata("vanished", Nexus.getInstance());

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
		Player target = user.getOnlinePlayer();
		for (Player viewer : OnlinePlayers.getAll()) {
			boolean hidden = false;
			if (Minigamer.of(target).getMatch() != null) {
				Minigamer targetMinigamer = Minigamer.of(target);
				Minigamer viewerMinigamer = Minigamer.of(viewer);

				if (viewerMinigamer.getMatch() != null)
					if (!viewerMinigamer.getMatch().getMechanic().canSee(viewerMinigamer, targetMinigamer))
						hidden = true;
			}

			// TODO Priority
			if (user.isVanished() && !Vanish.canSee(viewer, target))
				hidden = true;

			if (hidden)
				hidePlayer(target).from(viewer);
			else
				showPlayer(target).to(viewer);
		}
	}

	public static boolean isVanished(OptionalPlayer player) {
		if (player.getPlayer() == null)
			return false;

		return get(player.getPlayer()).isVanished();
	}

	// See PlayerUtils#canSee
	public static boolean canSee(Player viewer, Player target) {
		return !Vanish.isVanished(target) || viewer.hasPermission("pv.see");
	}

}

package gg.projecteden.nexus.features.commands.staff;

import de.myzelyam.api.vanish.VanishAPI;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Redirects.Redirect;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.godmode.GodmodeService;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.WorldGroup;
import org.bukkit.GameMode;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;

@Permission("group.staff")
@Redirect(from = "/nocheats", to = "/cheats off")
@Redirect(from = "/allcheats", to = "/cheats on")
public class CheatsCommand extends CustomCommand {
	private static final String PREFIX = StringUtils.getPrefix("Cheats");

	public CheatsCommand(CommandEvent event) {
		super(event);
	}

	@Path("<on|off>")
	void toggle(boolean enabled) {
		if (enabled) {
			on(player());
			send(PREFIX + "&aEnabled");
		} else {
			off(player());
			send(PREFIX + "&cDisabled");
		}
	}

	public static void off(Player player) {
		new GodmodeService().edit(player, godmode -> godmode.setEnabled(false));
		VanishAPI.showPlayer(player);
		WorldGuardEditCommand.off(player);

		if (WorldGroup.of(player) != WorldGroup.CREATIVE) {
			player.setGameMode(GameMode.SURVIVAL);
			player.setFallDistance(0);

			if (!player.getWorld().getEnvironment().equals(Environment.THE_END)) {
				player.setAllowFlight(false);
				player.setFlying(false);
			}
		}
	}

	public static void on(Player player) {
		new GodmodeService().edit(player, godmode -> godmode.setEnabled(true));

		if (player.hasPermission("essentials.gamemode.creative"))
			player.setGameMode(GameMode.CREATIVE);

		if (!player.getWorld().getEnvironment().equals(Environment.THE_END)) {
			player.setAllowFlight(true);
			player.setFlying(true);
		}

		VanishAPI.hidePlayer(player);
	}

}

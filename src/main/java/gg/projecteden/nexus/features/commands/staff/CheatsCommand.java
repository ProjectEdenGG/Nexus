package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.features.commands.GamemodeCommand;
import gg.projecteden.nexus.features.listeners.events.SubWorldGroupChangedEvent;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Redirects.Redirect;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.godmode.GodmodeService;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.worldgroup.SubWorldGroup;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.NoArgsConstructor;
import org.bukkit.GameMode;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static gg.projecteden.nexus.hooks.Hook.VANISH;

@NoArgsConstructor
@Permission(Group.STAFF)
@Redirect(from = "/nocheats", to = "/cheats off")
@Redirect(from = "/allcheats", to = "/cheats on")
public class CheatsCommand extends CustomCommand implements Listener {
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
		VANISH.showPlayer(player);
		WorldGuardEditCommand.off(player);

		if (WorldGroup.of(player) != WorldGroup.CREATIVE) {
			GamemodeCommand.setGameMode(player, GameMode.SURVIVAL);
			player.setFallDistance(0);

			if (!player.getWorld().getEnvironment().equals(Environment.THE_END)) {
				player.setAllowFlight(false);
				player.setFlying(false);
			}
		}
	}

	public static void on(Player player) {
		if (Rank.of(player).gte(Rank.ARCHITECT))
			new GodmodeService().edit(player, godmode -> godmode.setEnabled(true));

		if (player.hasPermission("essentials.gamemode.creative"))
			GamemodeCommand.setGameMode(player, GameMode.CREATIVE);

		if (!player.getWorld().getEnvironment().equals(Environment.THE_END)) {
			player.setAllowFlight(true);
			player.setFlying(true);
		}

		if (player.hasPermission("pv.use"))
			VANISH.hidePlayer(player);
	}

	@EventHandler
	public void on(SubWorldGroupChangedEvent event) {
		final Player player = event.getPlayer();

		if (event.getNewSubWorldGroup() != SubWorldGroup.STAFF_SURVIVAL)
			return;

		Tasks.wait(20, () -> CheatsCommand.off(player));
	}

}

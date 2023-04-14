package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.features.commands.GamemodeCommand;
import gg.projecteden.nexus.features.listeners.events.SubWorldGroupChangedEvent;
import gg.projecteden.nexus.features.vanish.Vanish;
import gg.projecteden.nexus.framework.commandsv2.annotations.command.Redirects.Redirect;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
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

@NoArgsConstructor
@Permission(Group.STAFF)
@Redirect(from = "/nocheats", to = "/cheats off")
@Redirect(from = "/allcheats", to = "/cheats on")
public class CheatsCommand extends CustomCommand implements Listener {
	private static final String PREFIX = StringUtils.getPrefix("Cheats");

	public CheatsCommand(CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Description("Toggles vanish, god, flight, WorldGuard edit, and gamemode")
	void toggle(boolean state) {
		if (state) {
			on(player());
			send(PREFIX + "&aEnabled");
		} else {
			off(player());
			send(PREFIX + "&cDisabled");
		}
	}

	public static void off(Player player) {
		new GodmodeService().edit(player, godmode -> godmode.setEnabled(false));
		Vanish.unvanish(player);
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
			Vanish.vanish(player);
	}

	@EventHandler
	public void on(SubWorldGroupChangedEvent event) {
		final Player player = event.getPlayer();

		if (event.getNewSubWorldGroup() != SubWorldGroup.STAFF_SURVIVAL)
			return;

		Tasks.wait(20, () -> CheatsCommand.off(player));
	}

}

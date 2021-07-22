package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Redirects.Redirect;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.godmode.Godmode;
import gg.projecteden.nexus.models.godmode.GodmodeService;
import gg.projecteden.nexus.utils.WorldGroup;
import org.bukkit.GameMode;
import org.bukkit.World.Environment;

@Permission("group.staff")
@Redirect(from = "/nocheats", to = "/cheats off")
@Redirect(from = "/allcheats", to = "/cheats on")
public class CheatsCommand extends CustomCommand {

	public CheatsCommand(CommandEvent event) {
		super(event);
	}

	@Path("<on|off>")
	void on(Boolean enabled) {
		GodmodeService godmodeService = new GodmodeService();
		Godmode godmode = godmodeService.get(player());
		if (enabled) {
			if (player().hasPermission("essentials.gamemode.creative"))
				player().setGameMode(GameMode.CREATIVE);
			if (!world().getEnvironment().equals(Environment.THE_END)) {
				player().setAllowFlight(true);
				player().setFlying(true);
			}
			godmode.setEnabled(true);
			godmodeService.save(godmode);
			runCommand("vanish on");

			send(PREFIX + "&aEnabled");
		} else {
			runCommand("vanish off");
			godmode.setEnabled(false);
			godmodeService.save(godmode);
			if (WorldGroup.of(player()) != WorldGroup.CREATIVE) {
				if (!world().getEnvironment().equals(Environment.THE_END)) {
					player().setAllowFlight(false);
					player().setFlying(false);
				}
				player().setGameMode(GameMode.SURVIVAL);
				player().setFallDistance(0);
			}
			runCommand("wgedit off");

			send(PREFIX + "&cDisabled");
		}
	}
}

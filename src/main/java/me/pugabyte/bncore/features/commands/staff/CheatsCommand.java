package me.pugabyte.bncore.features.commands.staff;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.annotations.Redirects.Redirect;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.godmode.Godmode;
import me.pugabyte.bncore.models.godmode.GodmodeService;
import org.bukkit.GameMode;

@Permission("group.staff")
@Redirect(from = "/nocheats", to = "/cheats off")
@Redirect(from = "/allcheats", to = "/cheats on")
public class CheatsCommand extends CustomCommand {

	public CheatsCommand(CommandEvent event) {
		super(event);
	}

	@Path("<on|off>")
	void on(Boolean enabled) {
		if (enabled) {
			if (player().hasPermission("essentials.gamemode.creative"))
				player().setGameMode(GameMode.CREATIVE);
			player().setAllowFlight(true);
			player().setFlying(true);
			((Godmode) new GodmodeService().get(player())).setEnabled(true);
			runCommand("vanish on");

			send(PREFIX + "&aEnabled");
		} else {
			runCommand("vanish off");
			((Godmode) new GodmodeService().get(player())).setEnabled(false);
			player().setGameMode(GameMode.SURVIVAL);
			player().setFallDistance(0);
			player().setAllowFlight(false);
			player().setFlying(false);

			send(PREFIX + "&cDisabled");
		}
	}
}

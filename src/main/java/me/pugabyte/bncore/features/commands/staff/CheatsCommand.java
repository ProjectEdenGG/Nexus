package me.pugabyte.bncore.features.commands.staff;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.annotations.Redirects.Redirect;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import org.bukkit.GameMode;

@Permission("group.staff")
@Redirect(from = "/nocheats", to = "/cheats off")
@Redirect(from = "/allcheats", to = "/cheats on")
public class CheatsCommand extends CustomCommand {

	public CheatsCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void help() {
		send("&c/cheats <on|off>");
	}

	@Path("on")
	void on() {
		if (player().hasPermission("essentials.gamemode.creative"))
			player().setGameMode(GameMode.CREATIVE);
		player().setAllowFlight(true);
		player().setFlying(true);
		BNCore.getEssentials().getUser(player().getUniqueId()).setGodModeEnabled(true);
		runCommand("vanish on");

		send(PREFIX + "Enabled");
	}

	@Path("off")
	void off() {
		runCommand("vanish off");
		BNCore.getEssentials().getUser(player().getUniqueId()).setGodModeEnabled(false);
		player().setGameMode(GameMode.SURVIVAL);
		player().setFallDistance(0);
		player().setAllowFlight(false);
		player().setFlying(false);

		send(PREFIX + "Disabled");
	}
}

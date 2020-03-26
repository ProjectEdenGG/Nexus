package me.pugabyte.bncore.features.commands.staff;

import com.earth2me.essentials.Essentials;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.annotations.Redirects.Redirect;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;

@Permission("group.staff")
@Redirect(from = "/nocheats", to = "/cheats off")
@Redirect(from = "/allcheats", to = "/cheats on")
public class CheatsCommand extends CustomCommand {
	Essentials essentials = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");

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
		essentials.getUser(player().getUniqueId()).setGodModeEnabled(true);
		runCommand("vanish on");

		send(PREFIX + "Enabled");
	}

	@Path("off")
	void off() {
		player().setGameMode(GameMode.SURVIVAL);
		player().setAllowFlight(false);
		player().setFlying(false);
		essentials.getUser(player().getUniqueId()).setGodModeEnabled(false);
		runCommand("vanish off");

		send(PREFIX + "Disabled");
	}
}

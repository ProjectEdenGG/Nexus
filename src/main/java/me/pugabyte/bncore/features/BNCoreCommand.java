package me.pugabyte.bncore.features;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.skript.SkriptFunctions;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class BNCoreCommand extends CustomCommand {
	public BNCoreCommand(CommandEvent event) {
		super(event);
	}

	@Path("getPlayer {offlineplayer}")
	void getPlayer(@Arg OfflinePlayer player) {
		reply(player.getName());
	}

	@Path("redtint {double} {double} {player}")
	void getPlayer(@Arg("0.5") double fadeTime, @Arg("10") double intensity, @Arg("self") Player player) {
		SkriptFunctions.redTint(player, fadeTime, intensity);
	}
}

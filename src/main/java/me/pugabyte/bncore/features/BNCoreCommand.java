package me.pugabyte.bncore.features;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.TabCompleterFor;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.nerds.Nerd;
import me.pugabyte.bncore.skript.SkriptFunctions;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class BNCoreCommand extends CustomCommand {
	public BNCoreCommand(CommandEvent event) {
		super(event);
	}

	// TODO: Move to appropriate place
	@TabCompleterFor({Player.class, OfflinePlayer.class, Nerd.class})
	List<String> playerTabComplete(String filter) {
		return Bukkit.getOnlinePlayers().stream()
				.filter(player -> player.getName().toLowerCase().startsWith(filter.toLowerCase()))
				.map(Player::getName)
				.collect(Collectors.toList());
	}

	@Path("getPlayer {offlineplayer}")
	void getPlayer(@Arg OfflinePlayer player) {
		reply(player.getName());
	}

	@Path("redtint {double} {double} {player}")
	void redTint(@Arg("0.5") double fadeTime, @Arg("10") double intensity, @Arg("self") Player player) {
		SkriptFunctions.redTint(player, fadeTime, intensity);
	}
}

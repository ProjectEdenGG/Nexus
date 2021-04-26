package me.pugabyte.nexus.features.commands;

import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.ConverterFor;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.annotations.Redirects.Redirect;
import me.pugabyte.nexus.framework.commands.models.annotations.TabCompleterFor;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Aliases({"gm", "egm", "egamemode", "gmt", "egmt"})
@Permission("essentials.gamemode")
@Redirect(from = {"/gms", "/gm0", "/egms", "/esurvival", "/survivalmode", "/esurvivalmode"}, to = "/gm s")
@Redirect(from = {"/gmc", "/gm1", "/egmc", "/creativemode", "/ecreativemode"}, to = "/gm c")
@Redirect(from = {"/gma", "/gm2", "/egma", "/adventure", "/eadventure", "/adventuremode", "/eadventuremode"}, to = "/gm a")
@Redirect(from = {"/gmsp", "/gm3", "/egmsp", "/spectator", "/spec"}, to = "/gm sp")
public class GamemodeCommand extends CustomCommand {

	public GamemodeCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<gamemode> [player]")
	void run(GameMode gamemode, @Arg("self") Player player) {
		if (!isSelf(player))
			checkPermission("essentials.gamemode.others");

		checkPermission("essentials.gamemode." + gamemode.name().toLowerCase());
		player.setGameMode(gamemode);
		send(player, PREFIX + "Switched to &e" + camelCase(gamemode));
		if (!isSelf(player))
			send(PREFIX + "Switched to &e" + camelCase(gamemode) + " &3for &e" + player.getName());
	}

	@ConverterFor(GameMode.class)
	GameMode convertToGameMode(String value) {
		if (value != null)
			if (value.equals("3") || value.startsWith("sp")) return GameMode.SPECTATOR;
			else if (value.equals("2") || value.startsWith("a")) return GameMode.ADVENTURE;
			else if (value.equals("1") || value.startsWith("c")) return GameMode.CREATIVE;
			else if (value.equals("0") || value.startsWith("s")) return GameMode.SURVIVAL;
		throw new InvalidInputException("Invalid gamemode");
	}

	@TabCompleterFor(GameMode.class)
	List<String> tabCompleteGameMode(String filter) {
		return Arrays.stream(GameMode.values())
				.filter(gamemode -> player().hasPermission("essentials.gamemode." + gamemode.name().toLowerCase()))
				.map(gamemode -> gamemode.name().toLowerCase())
				.collect(Collectors.toList());
	}

}

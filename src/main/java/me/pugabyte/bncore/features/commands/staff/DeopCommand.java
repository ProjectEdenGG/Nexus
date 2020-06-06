package me.pugabyte.bncore.features.commands.staff;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.ConverterFor;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.annotations.TabCompleterFor;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.permissions.ServerOperator;

import java.util.List;
import java.util.stream.Collectors;

public class DeopCommand extends CustomCommand {

	public DeopCommand(CommandEvent event) {
		super(event);
	}

	@Path("<player>")
	@Permission("group.admin")
	public void deop(ServerOperator op) {
		OfflinePlayer player = (OfflinePlayer) op;
		String name = player.getName();
		if (!player.isOp())
			error(name + " is not a server operator");

		player.setOp(false);
		send(PREFIX + name + " is no longer a server operator");

		if (player.isOnline() && !player.equals(player()))
			send(player.getPlayer(), PREFIX + "You are no longer a server operator");
	}

	@ConverterFor(ServerOperator.class)
	OfflinePlayer convertToServerOperator(String value) {
		return convertToOfflinePlayer(value);
	}

	@TabCompleterFor(ServerOperator.class)
	List<String> tabCompleteServerOperator(String filter) {
		return Bukkit.getOperators().stream().map(OfflinePlayer::getName).collect(Collectors.toList());
	}
}

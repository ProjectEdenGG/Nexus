package me.pugabyte.nexus.features.commands.staff.admin;

import me.pugabyte.nexus.features.chat.Chat;
import me.pugabyte.nexus.features.chat.Chat.StaticChannel;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.ConverterFor;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.annotations.TabCompleterFor;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.permissions.ServerOperator;

import java.util.List;
import java.util.stream.Collectors;

@Permission("group.admin")
public class DeopCommand extends CustomCommand {

	public DeopCommand(CommandEvent event) {
		super(event);
		PREFIX = StringUtils.getPrefix("Op");
	}

	@Path("<player>")
	public void deop(ServerOperator op) {
		OfflinePlayer player = (OfflinePlayer) op;

		String oper = name();
		String opee = player.getName();
		if (!player.isOp())
			error(opee + " is not op");

		player.setOp(false);
		if (isSelf(player))
			Chat.broadcastIngame(PREFIX + oper + " deopped themselves", StaticChannel.ADMIN);
		else
			Chat.broadcastIngame(PREFIX + oper + " deopped " + opee, StaticChannel.ADMIN);

		if (!player.equals(player()))
			send(player, PREFIX + "You are no longer op");
	}

	@ConverterFor(ServerOperator.class)
	OfflinePlayer convertToServerOperator(String value) {
		return convertToOfflinePlayer(value);
	}

	@TabCompleterFor(ServerOperator.class)
	List<String> tabCompleteServerOperator(String filter) {
		return Bukkit.getOperators().stream()
				.map(OfflinePlayer::getName)
				.filter(name -> name != null && name.toLowerCase().startsWith(filter.toLowerCase()))
				.collect(Collectors.toList());
	}
}

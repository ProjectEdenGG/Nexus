package gg.projecteden.nexus.features.commands.staff.admin;

import gg.projecteden.nexus.features.chat.Chat.Broadcast;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.permissions.ServerOperator;

import java.util.List;
import java.util.stream.Collectors;

@HideFromWiki
@Permission(Group.ADMIN)
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
			Broadcast.adminIngame().message(PREFIX + oper + " deopped themselves").send();
		else
			Broadcast.adminIngame().message(PREFIX + oper + " deopped " + opee).send();

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

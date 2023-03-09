package gg.projecteden.nexus.features.commands.staff.admin;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Permission(Group.ADMIN)
public class SudoCommand extends CustomCommand {

	public SudoCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<player> <command...>")
	@Description("Force a player to run a command")
	void run(Player player, String command) {
		PlayerUtils.runCommandAsOp(player, command);
		send("&3Made &e" + player.getName() + " &3run &e/" + command);
	}

}

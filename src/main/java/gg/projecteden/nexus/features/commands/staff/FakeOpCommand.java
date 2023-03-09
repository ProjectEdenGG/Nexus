package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import org.bukkit.entity.Player;

@Permission(Group.STAFF)
public class FakeOpCommand extends CustomCommand {

	public FakeOpCommand(CommandEvent event) {
		super(event);
	}

	@Path("<player>")
	@Description("Sends a player a message prentending they have been made OP")
	void fakeop(Player player) {
		send(player, "Opped " + player.getName());
	}

}

package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import org.bukkit.entity.Player;

@Permission("group.staff")
public class FakeOpCommand extends CustomCommand {

	public FakeOpCommand(CommandEvent event) {
		super(event);
	}

	@Path("<player>")
	void fakeop(Player player) {
		send(player, "Opped " + player.getName());
	}

}

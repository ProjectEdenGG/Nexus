package me.pugabyte.bncore.features.commands.staff;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import org.bukkit.entity.Player;

@Permission("group.staff")
public class FakeOpCommand extends CustomCommand {

	public FakeOpCommand(CommandEvent event) {
		super(event);
	}

	@Path("<player>")
	void slap(Player player) {
		player.sendMessage("Opped " + player.getName());
	}

}

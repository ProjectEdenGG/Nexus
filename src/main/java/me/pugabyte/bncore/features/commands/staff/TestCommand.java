package me.pugabyte.bncore.features.commands.staff;

import lombok.NoArgsConstructor;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import org.bukkit.event.Listener;

@NoArgsConstructor
@Permission("group.staff")
public class TestCommand extends CustomCommand implements Listener {

	public TestCommand(CommandEvent event) {
		super(event);
	}

	@Path("gravity fix")
	public void fixgravity() {
		player().setGravity(true);
	}



}

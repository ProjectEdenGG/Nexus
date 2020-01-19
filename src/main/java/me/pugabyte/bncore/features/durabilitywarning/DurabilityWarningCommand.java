package me.pugabyte.bncore.features.durabilitywarning;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

import static me.pugabyte.bncore.features.durabilitywarning.DurabilityWarning.disabledPlayers;

/**
 * @author Camaros
 */
@Aliases("dw")
@Permission("durabilitywarning.use")
public class DurabilityWarningCommand extends CustomCommand {

	public DurabilityWarningCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void toggle() {
		if (!disabledPlayers.contains(player())) {
			disabledPlayers.add(player());
			send(PREFIX + "Disabled");
		} else {
			disabledPlayers.remove(player());
			send(PREFIX + "Enabled");
		}
	}
}

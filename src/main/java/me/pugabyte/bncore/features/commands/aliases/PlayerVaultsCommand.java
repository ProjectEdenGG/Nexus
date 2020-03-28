package me.pugabyte.bncore.features.commands.aliases;

import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Fallback;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.WorldGroup;

@Fallback("playervaults")
@Aliases({"pv", "chest", "vault"})
public class PlayerVaultsCommand extends CustomCommand {

	public PlayerVaultsCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		if (WorldGroup.get(player()) != WorldGroup.SURVIVAL && !player().hasPermission("group.seniorstaff"))
			error("You can't open vaults here");
		fallback();
	}

}

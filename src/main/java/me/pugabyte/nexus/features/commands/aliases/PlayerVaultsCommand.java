package me.pugabyte.nexus.features.commands.aliases;

import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Fallback;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.WorldGroup;

@Fallback("playervaults")
@Aliases({"pv", "chest", "vault"})
public class PlayerVaultsCommand extends CustomCommand {

	public PlayerVaultsCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		if (WorldGroup.of(player()) != WorldGroup.SURVIVAL && !isSeniorStaff())
			error("You can't open vaults here");
		fallback();
	}

}

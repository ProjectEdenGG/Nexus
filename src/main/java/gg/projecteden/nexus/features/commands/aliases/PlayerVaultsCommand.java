package gg.projecteden.nexus.features.commands.aliases;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Fallback;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.WorldGroup;
import lombok.NonNull;

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

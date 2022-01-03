package gg.projecteden.nexus.features.minigames.commands;

import gg.projecteden.nexus.features.minigames.lobby.menu.GameMenu;
import gg.projecteden.nexus.features.minigames.models.mechanics.MechanicType;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import lombok.NonNull;

@Aliases("gl")
public class GamelobbyCommand extends CustomCommand {

	public GamelobbyCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void teleport() {
		runCommand("warp minigames");
	}

	@Permission(Permission.Group.ADMIN)
	@Path("testMenu <mechanic>")
	void testMenu(MechanicType mechanicType) {
		GameMenu.open(player(), mechanicType);
	}

}

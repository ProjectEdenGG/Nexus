package gg.projecteden.nexus.features.minigames.commands;

import gg.projecteden.nexus.features.minigames.lobby.menu.GameMenu;
import gg.projecteden.nexus.features.minigames.models.mechanics.MechanicGroup;
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
	@Path("testMenu <mechanicGroup> [mechanicType]")
	void testMenu(MechanicGroup group, MechanicType type) {
		GameMenu.open(player(), group, type);
	}

	@Permission(Permission.Group.ADMIN)
	@Path("getMechanicGroup <mechanicGroup>")
	void getMechanicType(MechanicGroup group) {
		StringBuilder builder = new StringBuilder();
		for (MechanicType type : MechanicType.values())
			if (type.getGroup() == group)
				builder.append(type.get().getName()).append(", ");
		send(builder.toString());
	}

}

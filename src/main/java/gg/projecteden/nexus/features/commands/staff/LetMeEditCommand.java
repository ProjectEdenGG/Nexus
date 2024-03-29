package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.features.vanish.Vanish;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.GameMode;
import org.bukkit.event.Listener;

@NoArgsConstructor
@Permission(Group.STAFF)
public class LetMeEditCommand extends CustomCommand implements Listener {

	public LetMeEditCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	@Description("Unvanish, enable WorldGuard edit, and switch to creative mode if applicable")
	void run() {
		Vanish.unvanish(player());
		WorldGuardEditCommand.on(player());

		if (hasPermission("essentials.gamemode.creative"))
			player().setGameMode(GameMode.CREATIVE);
	}

}

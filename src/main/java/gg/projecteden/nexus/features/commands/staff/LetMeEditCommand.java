package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.features.vanish.Vanish;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
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

	@Override
	@NoLiterals
	@Description("Unvanish, enable WorldGuard edit, and switch to creative mode if applicable")
	public void help() {
		Vanish.unvanish(player());
		WorldGuardEditCommand.on(player());

		if (hasPermission("essentials.gamemode.creative"))
			player().setGameMode(GameMode.CREATIVE);
	}

}

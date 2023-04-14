package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.framework.commandsv2.annotations.command.Redirects.Redirect;
import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.Optional;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Cooldown;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.utils.GameModeWrapper;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

@Permission(Group.SENIOR_STAFF)
@Redirect(from = "/suicide", to = "/kill")
public class KillCommand extends CustomCommand {

	public KillCommand(CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Cooldown(value = TickTime.MINUTE, bypass = Group.SENIOR_STAFF)
	@Description("Kill a player")
	void kill(@Optional("self") Player player) {
		if (!isStaff())
			if (WorldGroup.of(player()) != WorldGroup.SURVIVAL)
				error("You must be in the survival world to run this command");

		if (isStaff())
			runCommand("god off");

		if (!GameModeWrapper.of(player.getGameMode()).isSurvival())
			player().setGameMode(GameMode.SURVIVAL);

		player.damage(Short.MAX_VALUE);
		player.setHealth(0);
		send(PREFIX + "Killed " + player.getName());
	}
}

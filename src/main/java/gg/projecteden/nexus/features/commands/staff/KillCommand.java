package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Cooldown;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Redirects.Redirect;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Rank;
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

	@Path("<player>")
	@Cooldown(value = TickTime.MINUTE, bypass = Group.SENIOR_STAFF)
	@Description("Kill a player")
	public void kill(@Arg("self") Player player) {
		if (!isStaff())
			if (WorldGroup.of(player()) != WorldGroup.SURVIVAL)
				error("You must be in the survival world to run this command");

		if (Rank.of(player).isStaff())
			runCommand("god off");

		if (!GameModeWrapper.of(player.getGameMode()).isSurvival())
			player.setGameMode(GameMode.SURVIVAL);

		player.setHealth(0);
		send(PREFIX + "Killed " + player.getName());
	}
}

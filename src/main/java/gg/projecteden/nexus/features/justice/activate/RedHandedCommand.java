package gg.projecteden.nexus.features.justice.activate;

import gg.projecteden.nexus.features.chat.Chat;
import gg.projecteden.nexus.features.chat.Chat.StaticChannel;
import gg.projecteden.nexus.features.justice.misc._PunishmentCommand;
import gg.projecteden.nexus.features.vanish.Vanish;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.punishments.PunishmentType;
import gg.projecteden.nexus.models.punishments.Punishments;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.GameMode;

import java.util.List;

@Aliases("rh")
@Permission(Group.MODERATOR)
public class RedHandedCommand extends _PunishmentCommand {

	public RedHandedCommand(CommandEvent event) {
		super(event);
	}

	@Path("<players(s)>")
	@Description("Freeze a player or players, force them to local chat and unvanish to confront them")
	void player(@Arg(type = Punishments.class) List<Punishments> players) {
		punish(players);

		for (Punishments player : players) {
			Chat.setActiveChannel(player, StaticChannel.LOCAL);

			Tasks.wait(1, () -> send(json("&c&lClick here to let them continue. Type a reason to warn them").suggest("/youmaycontinue " + player.getName() + " ")));
		}

		if (player().getGameMode().equals(GameMode.SPECTATOR))
			if (isSeniorStaff())
				player().setGameMode(GameMode.CREATIVE);
			else
				player().setGameMode(GameMode.SURVIVAL);

		Vanish.unvanish(player());
		player().setFallDistance(0);
		PlayerUtils.setAllowFlight(player(), true, RedHandedCommand.class);
		PlayerUtils.setFlying(player(), true, RedHandedCommand.class);
		Chat.setActiveChannel(player(), StaticChannel.LOCAL);

		line();
	}

	@Override
	protected PunishmentType getType() {
		return PunishmentType.FREEZE;
	}

}

package gg.projecteden.nexus.features.justice.activate;

import gg.projecteden.nexus.features.chat.Chat;
import gg.projecteden.nexus.features.chat.Chat.StaticChannel;
import gg.projecteden.nexus.features.justice.misc._PunishmentCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.punishments.PunishmentType;
import gg.projecteden.nexus.models.punishments.Punishments;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.GameMode;

import java.util.List;

import static gg.projecteden.nexus.hooks.Hook.VANISH;

@Aliases("rh")
@Permission(Group.MODERATOR)
public class RedHandedCommand extends _PunishmentCommand {

	public RedHandedCommand(CommandEvent event) {
		super(event);
	}

	@Path("<players...>")
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

		VANISH.showPlayer(player());
		player().setFallDistance(0);
		player().setAllowFlight(true);
		player().setFlying(true);
		Chat.setActiveChannel(player(), StaticChannel.LOCAL);

		line();
	}

	@Override
	protected PunishmentType getType() {
		return PunishmentType.FREEZE;
	}

}

package me.pugabyte.nexus.features.commands.staff.moderator.punishments.freeze;

import de.myzelyam.api.vanish.VanishAPI;
import me.pugabyte.nexus.features.chat.Chat;
import me.pugabyte.nexus.features.chat.Chat.StaticChannel;
import me.pugabyte.nexus.features.commands.staff.moderator.punishments._PunishmentCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.chat.ChatService;
import me.pugabyte.nexus.models.punishments.Punishments;
import me.pugabyte.nexus.models.punishments.Punishments.Punishment.PunishmentType;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.GameMode;

import java.util.List;

@Aliases("rh")
@Permission("group.moderator")
public class RedHandedCommand extends _PunishmentCommand {
	private final ChatService chatService = new ChatService();
	private final StaticChannel local = StaticChannel.LOCAL;

	public RedHandedCommand(CommandEvent event) {
		super(event);
	}

	@Path("<players...>")
	void player(@Arg(type = Punishments.class) List<Punishments> players) {
		punish(players);
		for (Punishments player : players) {
			Chat.setActiveChannel(player, local);

			Tasks.wait(1, () -> send(json("&c&lClick here to let them continue. Type a reason to warn them").suggest("/youmaycontinue " + player.getName() + " ")));
		}

		if (player().getGameMode().equals(GameMode.SPECTATOR))
			if (isSeniorStaff())
				player().setGameMode(GameMode.CREATIVE);
			else
				player().setGameMode(GameMode.SURVIVAL);

		VanishAPI.hidePlayer(player());
		player().setAllowFlight(true);
		player().setFlying(true);
		Chat.setActiveChannel(player(), local);

		line();
	}

	@Override
	protected PunishmentType getType() {
		return PunishmentType.FREEZE;
	}

}

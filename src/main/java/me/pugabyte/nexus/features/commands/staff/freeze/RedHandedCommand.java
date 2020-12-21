package me.pugabyte.nexus.features.commands.staff.freeze;

import me.pugabyte.nexus.features.chat.ChatManager;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.chat.Channel;
import me.pugabyte.nexus.models.chat.ChatService;
import me.pugabyte.nexus.models.chat.Chatter;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.List;

@Aliases("rh")
@Permission("group.staff")
public class RedHandedCommand extends CustomCommand {
	ChatService chatService = new ChatService();
	Channel local = ChatManager.getChannel("l");

	public RedHandedCommand(CommandEvent event) {
		super(event);
	}

	@Path("<players...>")
	void player(@Arg(type = Player.class) List<Player> players) {
		for (Player player : players) {
			runCommand("freeze " + player.getName());

			((Chatter) chatService.get(player)).setActiveChannel(local);

			Tasks.wait(1, () -> send(json("&c&lClick here to let them continue. Type a reason to warn them").suggest("/youmaycontinue " + player.getName() + " ")));
		}

		if (player().getGameMode().equals(GameMode.SPECTATOR))
			if (isSeniorStaff())
				player().setGameMode(GameMode.CREATIVE);
			else
				player().setGameMode(GameMode.SURVIVAL);

		runCommand("vanish off");
		player().setAllowFlight(true);
		player().setFlying(true);
		((Chatter) chatService.get(player())).setActiveChannel(local);

		line();
	}

}

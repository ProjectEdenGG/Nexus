package me.pugabyte.bncore.features.commands.staff.freeze;

import me.pugabyte.bncore.features.chat.ChatManager;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.chat.Channel;
import me.pugabyte.bncore.models.chat.ChatService;
import me.pugabyte.bncore.models.chat.Chatter;
import me.pugabyte.bncore.utils.Tasks;
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
			if (player().getGameMode().equals(GameMode.SPECTATOR))
				if (player().hasPermission("group.seniorstaff"))
					player().setGameMode(GameMode.CREATIVE);
				else
					player().setGameMode(GameMode.SURVIVAL);
			((Chatter) chatService.get(player)).setActiveChannel(local);

			Tasks.wait(1, () -> send(json("&c&lClick here to let them continue. Type a reason to warn them").suggest("/youmaycontinue " + player.getName() + " ")));
		}
		runCommand("vanish off");
		player().setAllowFlight(true);
		player().setFlying(true);
		((Chatter) chatService.get(player())).setActiveChannel(local);

		line();
	}

}

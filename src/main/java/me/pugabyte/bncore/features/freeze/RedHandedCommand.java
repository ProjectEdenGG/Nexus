package me.pugabyte.bncore.features.freeze;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.setting.Setting;
import me.pugabyte.bncore.models.setting.SettingService;
import me.pugabyte.bncore.utils.Tasks;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.List;

@Aliases("rh")
@Permission("group.staff")
public class RedHandedCommand extends CustomCommand {
	SettingService service = new SettingService();

	public RedHandedCommand(CommandEvent event) {
		super(event);
	}

	@Path("<players...>")
	void player(@Arg(type = Player.class) List<Player> players) {
		for (Player player : players) {
			Setting setting = service.get(player, "frozen");
			if (setting.getBoolean())
				error(player.getName() + " is already frozen. Click on the red message that appeared when you used this the first time.");
			line(2);
			if (player().getGameMode().equals(GameMode.SPECTATOR))
				if (player().hasPermission("group.seniorstaff"))
					player().setGameMode(GameMode.CREATIVE);
				else
					player().setGameMode(GameMode.SURVIVAL);
			runCommand("freeze " + player.getName());
			runCommand("forcechannel " + player.getName() + " l");
			Tasks.wait(1, () -> send(json("&c&lClick here to let them continue. Type a reason to warn them").suggest("youmaycontinue " + player.getName() + " ")));
		}
		runCommand("vanish off");
		runCommand("fly on");
		runCommand("ch l");
		line();
	}

}

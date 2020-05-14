package me.pugabyte.bncore.features.commands.poof;

import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.framework.exceptions.postconfigured.PlayerNotOnlineException;
import me.pugabyte.bncore.utils.Utils.RelativeLocation;
import me.pugabyte.bncore.utils.Utils.RelativeLocation.Modify;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import static me.pugabyte.bncore.utils.Utils.getLocation;

@Aliases({"tp", "tppos"})
public class TeleportCommand extends CustomCommand {

	public TeleportCommand(@NonNull CommandEvent event) {
		super(event);
	}

	private boolean isCoord(String arg) {
		if (arg.equals("~")) return true;
		arg = arg.replace("~", "");
		return isDouble(arg);
	}

	@Path("<player> [player]")
	void run(@Arg(tabCompleter = OfflinePlayer.class) String arg1, @Arg(tabCompleter = OfflinePlayer.class) String arg2) {
		if (!player().hasPermission("group.staff")) {
			runCommand("tpa " + argsString());
			return;
		}

		if (isCoord(arg(1)) && isCoord(arg(2)) && isCoord(arg(3))) {
			Location location = player().getLocation();
			Modify modifier = RelativeLocation.modify(location).x(arg(1)).y(arg(2)).z(arg(3));
			if (arg(4) != null && arg(5) != null && isCoord(arg(4)) && isCoord(arg(5))) {
				modifier.yaw(arg(4)).pitch(arg(5));
				if (arg(6) != null) {
					if (Bukkit.getWorld(arg(6)) == null)
						error("World &e" + arg(6) + " &cnot found");
					location.setWorld(Bukkit.getWorld(arg(6)));
				}
			} else if (!isNullOrEmpty(arg(4))) {
				if (Bukkit.getWorld(arg(4)) == null)
					error("World &e" + arg(4) + " &cnot found");
				else
					location.setWorld(Bukkit.getWorld(arg(4)));
			}

			player().teleport(modifier.update(), TeleportCause.COMMAND);
		} else if (isOfflinePlayerArg(1)) {
			OfflinePlayer player1 = offlinePlayerArg(1);
			Location location1 = getLocation(player1);
			if (isOfflinePlayerArg(2)) {
				OfflinePlayer player2 = offlinePlayerArg(2);
				if (player1.isOnline()) {
					player1.getPlayer().teleport(getLocation(player2), TeleportCause.COMMAND);
					send(PREFIX + "Poofing to &e" + player2.getName() + (player2.isOnline() ? "" : " &3(Offline)"));
				} else
					throw new PlayerNotOnlineException(player1);
			} else {
				player().teleport(location1, TeleportCause.COMMAND);
				send(PREFIX + "Poofing to &e" + player1.getName() + (player1.isOnline() ? "" : " &3(Offline)"));
			}
		} else {
			send("&c/" + getAliasUsed() + " <player> [player]");
			send("&c/" + getAliasUsed() + " <x> <y> <z> [yaw] [pitch] [world]");
		}
	}

}

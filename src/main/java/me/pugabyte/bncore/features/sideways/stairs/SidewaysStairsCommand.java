package me.pugabyte.bncore.features.sideways.stairs;

import me.pugabyte.bncore.features.sideways.stairs.models.SidewaysStairsPlayer;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import org.bukkit.ChatColor;

import static me.pugabyte.bncore.features.sideways.stairs.SidewaysStairs.playerData;

/**
 * @author Camaros
 */

@Aliases("sws")
public class SidewaysStairsCommand extends CustomCommand {
	private SidewaysStairsPlayer swsPlayer;

	SidewaysStairsCommand(CommandEvent event) {
		super(event);
		if (!playerData.containsKey(player())) {
			playerData.put(player(), new SidewaysStairsPlayer(player()));
		}
		swsPlayer = playerData.get(player());
	}

	@Path
	void help() {
		send(PREFIX + "Commands:");
		send(ChatColor.RED + "/sws toggle &7- Turn SWS on or off");
		send(ChatColor.RED + "/sws angle <number> &7- Set the angle of the stairs to be placed (0-7)");
		send(ChatColor.RED + "/sws rotate &7- Rotate the stair angle");
		send(ChatColor.RED + "/sws copy &7- Copy the angle of an existing stair");
		send(ChatColor.RED + "/sws upsidedown [true|false] &7- Toggle upsidedown stairs");
	}

	@Path("toggle")
	void toggle() {
		toggle(!swsPlayer.isEnabled());
	}

	@Path("<true|false>")
	void toggle(@Arg boolean enable) {
		swsPlayer.setEnabled(enable);
		send(PREFIX + (swsPlayer.isEnabled() ? "Enabled" : "Disabled"));
	}

	@Path("(set|angle|setangle)")
	void setAngle() {
		send(PREFIX + "/sws angle <number> &7- Set the angle of the stairs to be placed (0-7)");
	}

	@Path("(set|angle|setangle) <angle>")
	void setAngle(@Arg byte angle) {
		swsPlayer.trySetAngle(angle);
		swsPlayer.setAction("set_angle");
		swsPlayer.setEnabled(true);
		send(PREFIX + "Angle successfully set to " + angle);
	}

	@Path("copy")
	void copy() {
		swsPlayer.setAction("copy");
		send(PREFIX + "Right click a stair block to copy its angle.");
	}

	@Path("rotate")
	void rotate() {
		swsPlayer.setEnabled(true);
		send(PREFIX + "Angle changed to " + swsPlayer.rotate());
	}

	@Path("upsidedown")
	void upsideDown() {
		upsideDown(swsPlayer.getAction().equals("disable_upsidedown_placement"));
	}

	@Path("upsidedown <true|false>")
	void upsideDown(@Arg boolean allow) {
		swsPlayer.setAction(allow ? "" : "disable_upsidedown_placement");
		swsPlayer.setEnabled(!allow);
		send(PREFIX + "Upsidedown stair placement " + (allow ? "enabled" : "disabled."));
	}

}

package gg.projecteden.nexus.features.minigolf;

import gg.projecteden.nexus.features.minigolf.models.GolfBall;
import gg.projecteden.nexus.features.minigolf.models.GolfBallColor;
import gg.projecteden.nexus.features.minigolf.models.MiniGolfUser;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.StringUtils;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.block.data.Rotatable;

@HideFromWiki // TODO?
@Permission(Group.STAFF)
public class JMiniGolfCommand extends CustomCommand {
	MiniGolfUser user;

	public JMiniGolfCommand(CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent()) {
			user = MiniGolfUtils.getUser(uuid());
			if (user == null) {
				user = new MiniGolfUser(uuid(), GolfBallColor.WHITE);
				user.sendMessage("new user");
			} else {
				user.sendMessage("get user");
			}

		}
	}

	@Path("play")
	void play() {
		if (MiniGolf.isPlaying(user)) {
			send("already playing minigolf");
			return;
		}

		MiniGolf.join(user);
		user.giveKit();
		send("started playing minigolf");
	}

	@Path("join")
	void join() {
		if (MiniGolf.isPlaying(user)) {
			send("already playing minigolf");
			return;
		}

		MiniGolf.join(user);
		send("started playing minigolf");
	}

	@Path("quit")
	void quit() {
		if (!MiniGolf.isPlaying(user)) {
			send("not playing minigolf");
			return;
		}

		MiniGolf.quit(user);
		send("stopped playing minigolf");
	}

	@Path("kit")
	void kit() {
		if (!MiniGolf.isPlaying(user)) {
			send("not playing minigolf");
			return;
		}

		user.giveKit();
		send("giving kit");
	}

	@Path("setColor <color>")
	void setColor(GolfBallColor color) {
		if (!MiniGolf.isPlaying(user)) {
			send("not playing minigolf");
			return;
		}

		user.setGolfBallColor(color);
	}

	@Path("debug [enable]")
	void debug(Boolean enable) {
		if (enable == null)
			enable = !user.isDebug();

		if (!MiniGolf.isPlaying(user)) {
			send("not playing minigolf");
			return;
		}

		user.setDebug(enable);
		send("set debug to: " + user.isDebug());
	}

	@Permission(Group.ADMIN)
	@Path("getRotation")
	void getRotation() {
		Block block = getTargetBlockRequired();
		Skull skull = (Skull) block.getState();
		Rotatable rotatable = (Rotatable) skull.getBlockData();
		BlockFace facing = rotatable.getRotation();

		send("BlockFace: " + facing);
	}

	@Path("debugBall")
	void debugBall() {
		if (!MiniGolf.isPlaying(user)) {
			send("not playing minigolf");
			return;
		}

		GolfBall golfBall = user.getGolfBall();

		send("Alive: " + golfBall.isAlive());
		send("Active: " + golfBall.isActive());
		send("Loc: " + StringUtils.getPerciseCoordinateString(golfBall.getLocation(), 2));
		send("Last Loc: " + (golfBall.getLastLocation() == null ? "null" : StringUtils.getPerciseCoordinateString(golfBall.getLastLocation(), 2)));
		send("Vel: " + golfBall.getVelocity());
		send("Below: " + golfBall.getBlockBelow().getType());
		send("Inside: " + golfBall.getBlock().getType());
	}
}

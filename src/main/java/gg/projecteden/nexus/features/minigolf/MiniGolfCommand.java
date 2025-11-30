package gg.projecteden.nexus.features.minigolf;

import gg.projecteden.nexus.features.minigolf.models.GolfBallColor;
import gg.projecteden.nexus.features.minigolf.models.blocks.ModifierBlockType;
import gg.projecteden.nexus.features.minigolf.models.blocks.TeleportBlock.TeleportBlockArgs;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.minigolf.GolfBall;
import gg.projecteden.nexus.models.minigolf.MiniGolfUser;
import gg.projecteden.nexus.models.minigolf.MiniGolfUserService;
import gg.projecteden.nexus.utils.StringUtils;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Rotatable;

/*
	TODO:
		ADD COMMAND TO SEE ALL MODIFIER BLOCKS & SKULLS AND ABILITY TO GRAB THEM
 */
@HideFromWiki // TODO?
@Permission(Group.STAFF)
public class MiniGolfCommand extends CustomCommand {
	private final MiniGolfUserService userService = new MiniGolfUserService();
	private MiniGolfUser user;

	public MiniGolfCommand(CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent())
			user = userService.get(player());
	}

	@Path("play")
	void play() {
		MiniGolf.join(user);
		user.giveKit();
		send(PREFIX + "You have joined MiniGolf");
	}

	@Path("quit")
	void quit() {
		MiniGolf.quit(user);
		send(PREFIX + "You have quit MiniGolf");
	}

	@Path("kit")
	void kit() {
		if (!user.isPlaying()) {
			send(PREFIX + "You are not playing MiniGolf");
			return;
		}

		user.giveKit();
		send(PREFIX + "MiniGolf kit given to you");
	}

	@Path("setColor <color>")
	void setColor(GolfBallColor color) {
		if (!user.isPlaying()) {
			send("not playing minigolf");
			return;
		}

		user.setGolfBallColor(color);
	}

	@Permission(Group.STAFF)
	@Path("debug [enable]")
	void debug(Boolean enable) {
		if (enable == null)
			enable = !user.isDebug();

		if (!user.isPlaying()) {
			send("not playing minigolf");
			return;
		}

		user.setDebug(enable);
		send("set debug to: " + user.isDebug());
	}

	@Path("config")
	void config() {
		new MiniGolfConfigMenu().open(player());
	}

	@Permission(Group.STAFF)
	@Path("getTeleportItem [--speed]")
	void getTeleportItem(@Switch Double speed) {
		Block block = getTargetBlockRequired();
		if (!ModifierBlockType.TELEPORT.getMaterials().contains(block.getType()))
			error("Must be looking at a Teleport Block");

		BlockData blockData = block.getBlockData();
		if (blockData instanceof Directional directional)
			giveItem(TeleportBlockArgs.getItem(block, directional, speed));
		else
			error("That block is not directional ??");
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

	@Permission(Group.STAFF)
	@Path("debugBall")
	void debugBall() {
		if (!user.isPlaying()) {
			send("not playing minigolf");
			return;
		}

		GolfBall golfBall = user.getGolfBall();

		send("Alive: " + golfBall.isAlive());
		send("Active: " + golfBall.isActive());
		send("Course: " + golfBall.getCourseId());
		send("Hole: " + golfBall.getHoleId());
		send("Loc: " + StringUtils.xyzDecimal(golfBall.getBallLocation(), 2));
		send("Last Loc: " + (golfBall.getLastLocation() == null ? "null" : StringUtils.xyzDecimal(golfBall.getLastLocation(), 2)));
		send("Vel: " + golfBall.getVelocity());
		send("Below: " + golfBall.getBlockBelow().getType());
		send("Inside: " + golfBall.getBlock().getType());
	}
}

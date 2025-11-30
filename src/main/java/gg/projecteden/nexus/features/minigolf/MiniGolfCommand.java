package gg.projecteden.nexus.features.minigolf;

import gg.projecteden.nexus.features.minigolf.menus.GolfBallParticleMenu;
import gg.projecteden.nexus.features.minigolf.menus.GolfBallStyleMenu;
import gg.projecteden.nexus.features.minigolf.menus.MiniGolfConfigMenu;
import gg.projecteden.nexus.features.minigolf.menus.ScorecardBookMenu;
import gg.projecteden.nexus.features.minigolf.models.GolfBallParticle;
import gg.projecteden.nexus.features.minigolf.models.GolfBallStyle;
import gg.projecteden.nexus.features.minigolf.models.blocks.ModifierBlockType;
import gg.projecteden.nexus.features.minigolf.models.blocks.TeleportBlock.TeleportBlockArgs;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.minigolf.GolfBall;
import gg.projecteden.nexus.models.minigolf.MiniGolfConfig.MiniGolfCourse;
import gg.projecteden.nexus.models.minigolf.MiniGolfConfigService;
import gg.projecteden.nexus.models.minigolf.MiniGolfUser;
import gg.projecteden.nexus.models.minigolf.MiniGolfUserService;
import gg.projecteden.nexus.utils.StringUtils;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Rotatable;

import java.util.List;

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
	@Description("Play MiniGolf")
	void play() {
		MiniGolf.join(user);
		send(PREFIX + "You have joined MiniGolf");
	}

	@Path("quit")
	@Description("Quit MiniGolf")
	void quit() {
		MiniGolf.quit(user);
		send(PREFIX + "You have quit MiniGolf");
	}

	@Path("kit")
	@Description("Get the MiniGolf kit")
	void kit() {
		if (!user.isPlaying()) {
			send(PREFIX + "You are not playing MiniGolf");
			return;
		}

		user.giveKit();
		send(PREFIX + "MiniGolf kit given to you");
	}

	@Path("ball style [style]")
	@Description("Set your golf ball style")
	void ball_style(GolfBallStyle style) {
		var course = user.getCurrentCourseRequired();

		if (style == null)
			new GolfBallStyleMenu(user, course).open(player());
		else {
			if (!user.getAvailableStyles(course).contains(style))
				error("You have not unlocked that golf ball style");

			user.setStyle(course, style);
			user.replaceGolfBallInInventory();
			send(PREFIX + "Activated " + camelCase(style) + " Golf Ball");
		}
	}

	@Path("ball particle [particle]")
	void ball_particle(GolfBallParticle particle) {
		var course = user.getCurrentCourseRequired();

		if (particle == null)
			new GolfBallParticleMenu(user, course).open(player());
		else {
			if (!user.getAvailableParticles(course).contains(particle))
				error("You have not unlocked that golf ball particle");

			user.setParticle(course, particle);
			send(PREFIX + "Activated " + camelCase(particle) + " Golf Ball Particle");
		}
	}

	@Path("scorecard <course> [page]")
	void scorecard(MiniGolfCourse course, @Arg("1") int page) {
		new ScorecardBookMenu(user, course, page, user.getCurrentScorecard(course)).open();
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

	@TabCompleterFor(MiniGolfCourse.class)
	List<String> tabCompleteMiniGolfCourse(String filter) {
		return new MiniGolfConfigService().get0().getCourses().stream()
			.map(MiniGolfCourse::getName)
			.filter(name -> name.toLowerCase().startsWith(filter.toLowerCase()))
			.toList();
	}

	@ConverterFor(MiniGolfCourse.class)
	MiniGolfCourse convertToMiniGolfCourse(String value) {
		return new MiniGolfConfigService().get0().getCourse(value);
	}
}

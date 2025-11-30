package gg.projecteden.nexus.features.minigolf;

import com.destroystokyo.paper.ParticleBuilder;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.minigolf.listeners.InteractListener;
import gg.projecteden.nexus.features.minigolf.listeners.ProjectileListener;
import gg.projecteden.nexus.features.minigolf.models.GolfBallStyle;
import gg.projecteden.nexus.features.minigolf.models.blocks.ModifierBlock;
import gg.projecteden.nexus.features.minigolf.models.blocks.ModifierBlockType;
import gg.projecteden.nexus.features.minigolf.models.events.MiniGolfBallModifierBlockEvent;
import gg.projecteden.nexus.features.minigolf.models.events.MiniGolfBallMoveEvent;
import gg.projecteden.nexus.features.minigolf.models.events.MiniGolfUserJoinEvent;
import gg.projecteden.nexus.features.minigolf.models.events.MiniGolfUserQuitEvent;
import gg.projecteden.nexus.features.particles.ParticleUtils;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.models.minigolf.GolfBall;
import gg.projecteden.nexus.models.minigolf.MiniGolfUser;
import gg.projecteden.nexus.models.minigolf.MiniGolfUserService;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

/*
 TODO:
  - better entity collision detection than ProjectileHitEvent
  - Allow golfball to not go out of bounds in regions using regex
  - Golfball can get stuck on boost/conveyor blocks
  -
  - on place ball, get par, save in a list "cache" (world, region id, par)
  - when the ball is placed for the first time, or has stopped moving, spawn a display entity instead --> fixes visual float into ground?
  - splash sound when ball dies in water
  - when ball respawns from out of bounce, it gets stuck in some kind of loop, only fix is to whistle 1 time, and then you can hit it again
  - every 20 ticks, loop all balls and check if they are within a course region, if not --> out of bounds

	Minigolf:
	- Ball Styles
		- Event Store
	- Scorecard
		- Redesign in dialogs?
		- Add ability to reset current scorecard
*/

public class MiniGolf extends Feature {
	public static String PREFIX = StringUtils.getPrefix("MiniGolf");

	public static final double MAX_VELOCITY = 1.5;
	public static final double MIN_VELOCITY = 0.01;
	public static final double FLOOR_OFFSET = 0.05;
	public static final String HOLE_REGION_REGEX = ".*minigolf_hole_[\\d]+.*$";

	public static final Map<UUID, Float> POWER_MAP = new HashMap<>();

	public static final List<ItemModelType> KIT_MODELS = List.of(
		ItemModelType.MINIGOLF_PUTTER,
		ItemModelType.MINIGOLF_WEDGE,
		ItemModelType.MINIGOLF_WHISTLE,
		ItemModelType.MINIGOLF_SCORECARD,
		ItemModelType.MINIGOLF_BALL
	);

	@Override
	public void onStart() {
		new InteractListener();
		new ProjectileListener();
		miniGolfTask();
		playerTask();
	}

	@Override
	public void onStop() {
		var userService = new MiniGolfUserService();
		for (var user : userService.getOnline())
			userService.save(user);
	}

	public static void join(MiniGolfUser user) {
		if (user.isPlaying()) {
			user.debug("already playing minigolf");
			return;
		}

		var course = user.getCurrentCourse();
		if (course == null) {
			user.debug("not in a course region");
			return;
		}

		// Join Event
		var userJoinEvent = new MiniGolfUserJoinEvent(user, course);
		if (!userJoinEvent.callEvent()) {
			user.debug("join event cancelled");
		}

		user.setPlaying(true);
		user.giveKit();
	}

	public static void quit(MiniGolfUser user) {
		if (!user.isPlaying()) {
			user.debug("not playing minigolf");
			return;
		}

		// Quit Event
		MiniGolfUserQuitEvent userQuitEvent = new MiniGolfUserQuitEvent(user);
		if (!userQuitEvent.callEvent()) {
			user.debug("quit event cancelled");
			return;
		}

		if (user.getGolfBall() != null) {
			user.getGolfBall().remove();
		}

		for (ItemStack content : user.getOnlinePlayer().getInventory().getContents()) {
			if (isNullOrAir(content))
				continue;

			for (ItemModelType kitModel : KIT_MODELS)
				if (kitModel.is(content))
					content.setAmount(0);
		}

		user.setPlaying(false);
	}

	private void playerTask() {
		Tasks.repeat(TickTime.SECOND.x(5), TickTime.TICK, () -> {
			var userService = new MiniGolfUserService();
			for (var user : userService.getOnline()) {
				if (!user.isPlaying())
					continue;

				if (!user.canHitBall())
					continue;

				float amount = user.getOnlinePlayer().getPing() < 200 ? 0.04F : 0.02F;

				float exp = POWER_MAP.getOrDefault(user.getUuid(), 0.0F);
				exp += amount;
				if (exp > 1.00)
					exp = 0.0F;

				POWER_MAP.put(user.getUuid(), exp);

				user.getOnlinePlayer().sendExperienceChange(exp, 0); // TODO: PLAYER EXP NEVER FILLS TO 100%
			}
		});
	}

	private void miniGolfTask() {
		Tasks.repeat(0, TickTime.TICK, () -> {
			MiniGolfUserService userService = new MiniGolfUserService();
			for (var user : userService.getOnline()) {
				if (Bukkit.getCurrentTick() % 20 == 0)
					userService.save(user);

				var golfBall = user.getGolfBall();
				if (golfBall == null)
					continue;

				Snowball ball = golfBall.getSnowball();
				if (ball == null)
					continue;

				if (!ball.isValid()) {
					golfBall.remove();
					user.setGolfBall(null);
					continue;
				}

				Location lastLoc = golfBall.getLastLocation();
				Location curLoc = ball.getLocation();
				if (lastLoc == null || lastLoc.equals(curLoc))
					continue;

				MiniGolfBallMoveEvent ballMoveEvent = new MiniGolfBallMoveEvent(golfBall, lastLoc, curLoc);
				if (!ballMoveEvent.callEvent()) {
					ball.teleportAsync(lastLoc);
					ball.setVelocity(ball.getVelocity());
				}

				Block below = golfBall.getBlockBelow();
				Material belowType = below.getType();
				applyRollModifiers(golfBall, below, belowType);

				spawnParticles(user);
			}
		});
	}

	private static void spawnParticles(MiniGolfUser user) {
		var ball = user.getGolfBall().getSnowball();
		if (ball == null)
			return;

		var miniGolfParticle = user.getParticle();
		if (miniGolfParticle == null)
			return;

		if (!(ball.getVelocity().lengthSquared() > 0.0001))
			return;

		try {
			Particle particle = miniGolfParticle.getParticle();
			ParticleBuilder particleBuilder = new ParticleBuilder(particle)
				.location(ball.getLocation().add(0, FLOOR_OFFSET, 0))
				.count(1)
				.extra(0);

			if (particle.equals(Particle.DUST)) {
				if (user.getStyle().equals(GolfBallStyle.RAINBOW)) {
					int[] rgb = ParticleUtils.incRainbow(ball.getTicksLived());
					DustOptions dustOptions = ParticleUtils.newDustOption(particle, rgb[0], rgb[1], rgb[2]);
					particleBuilder.data(dustOptions);
				} else
					particleBuilder.color(user.getStyle().getColor().getBukkitColor());
			}

			particleBuilder.spawn();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	protected static void applyRollModifiers(GolfBall golfBall, Block below, Material belowType) {
		for (ModifierBlockType modifierBlockType : ModifierBlockType.values()) {
			ModifierBlock modifierBlock = modifierBlockType.getModifierBlock();

			if (checkApplies(below, belowType, modifierBlockType, modifierBlock)) {
				MiniGolfBallModifierBlockEvent modifierBlockEvent = new MiniGolfBallModifierBlockEvent(golfBall, modifierBlockType);
				if (modifierBlockEvent.callEvent()) {
					modifierBlock.handleRoll(golfBall, below);
					return;
				}
			}
		}
	}

	public static void applyBounceModifiers(GolfBall golfBall, Block hitBlock, Material hitMaterial, BlockFace blockFace) {
		for (ModifierBlockType modifierBlockType : ModifierBlockType.values()) {
			ModifierBlock modifierBlock = modifierBlockType.getModifierBlock();

			if (checkApplies(hitBlock, hitMaterial, modifierBlockType, modifierBlock)) {
				MiniGolfBallModifierBlockEvent modifierBlockEvent = new MiniGolfBallModifierBlockEvent(golfBall, modifierBlockType);
				if (modifierBlockEvent.callEvent()) {
					modifierBlock.handleBounce(golfBall, hitBlock, blockFace);
					break;
				}
			}
		}
	}

	private static boolean checkApplies(Block block, Material blockType, ModifierBlockType modifierBlockType, ModifierBlock modifierBlock) {
		boolean applies = modifierBlockType.equals(ModifierBlockType.DEFAULT) || modifierBlock.getMaterials().contains(blockType);
		return applies && modifierBlock.additionalContext(block);
	}

}

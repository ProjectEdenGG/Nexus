package gg.projecteden.nexus.features.minigolf;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.minigolf.listeners.InteractListener;
import gg.projecteden.nexus.features.minigolf.listeners.ProjectileListener;
import gg.projecteden.nexus.features.minigolf.models.GolfBall;
import gg.projecteden.nexus.features.minigolf.models.MiniGolfUser;
import gg.projecteden.nexus.features.minigolf.models.blocks.ModifierBlock;
import gg.projecteden.nexus.features.minigolf.models.blocks.ModifierBlockType;
import gg.projecteden.nexus.features.minigolf.models.events.MiniGolfBallModifierBlockEvent;
import gg.projecteden.nexus.features.minigolf.models.events.MiniGolfBallMoveEvent;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.utils.Tasks;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/*
 TODO:
  - persistent data
  - better entity collision detection than ProjectileHitEvent
  - don't bounce off of paintings or itemframes
  - Allow golfball to not go out of bounds in regions using regex
  - Golfball can get stuck on boost/conveyor blocks
  - Golfballs that roll off of slabs, stay in the air
  -
  - Make Teleport blocks parse an item in the block instead of a sign below it
  	- Add more lines such as yaw and speed
  	- Make a command to generate this item
*/

public class MiniGolf extends Feature {
	@Getter
	private static final double maxVelocity = 1.5;
	@Getter
	private static final double minVelocity = 0.01;
	@Getter
	private static final double floorOffset = 0.05;
	@Getter
	private static final String holeRegionRegex = ".*minigolf_hole_[\\d]+.*$";

	@Getter
	private static final Set<MiniGolfUser> users = new HashSet<>();
	@Getter
	private static final Set<GolfBall> golfBalls = new HashSet<>();
	@Getter
	private static final Map<UUID, Float> powerMap = new HashMap<>();


	@Override
	public void onStart() {
		new InteractListener();
		new ProjectileListener();
		miniGolfTask();
		playerTask();
	}

	@Override
	public void onStop() {
		for (GolfBall golfBall : new ArrayList<>(golfBalls)) {
			if (golfBall.isAlive()) {
				golfBall.getSnowball().remove();
			}
		}
	}

	public static void join(MiniGolfUser user) {
		getUsers().add(user);
	}

	public static void quit(MiniGolfUser user) {
		if (user.getGolfBall() != null) {
			user.getGolfBall().remove();
		}

		getGolfBalls().remove(user.getGolfBall());
		getUsers().remove(user);
	}

	public static boolean isPlaying(MiniGolfUser user) {
		return MiniGolf.getUsers().contains(user);
	}

	private void playerTask() {
		Tasks.repeat(TickTime.SECOND.x(5), TickTime.TICK, () -> {
			for (MiniGolfUser user : new HashSet<>(users)) {
				if (!user.canHitBall())
					continue;

				Player player = user.getOnlinePlayer();
				float amount = player.getPing() < 200 ? 0.04F : 0.02F;

				float exp = powerMap.getOrDefault(user.getUuid(), 0.0F);
				exp += amount;
				if (exp > 1.00)
					exp = 0.0F;

				powerMap.put(user.getUuid(), exp);

				player.sendExperienceChange(exp, 0); // TODO: PLAYER EXP NEVER FILLS TO 100%

			}
		});
	}

	private void miniGolfTask() {
		Tasks.repeat(0, TickTime.TICK, () -> {
			if (golfBalls.isEmpty())
				return;

			for (GolfBall golfBall : new ArrayList<>(golfBalls)) {
				Snowball ball = golfBall.getSnowball();
				if (ball == null)
					continue;

				if (!ball.isValid()) {
					golfBalls.remove(golfBall);
					golfBall.remove();
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
			}
		});
	}

	protected static void applyRollModifiers(GolfBall golfBall, Block below, Material belowType) {
		for (ModifierBlockType modifierBlockType : ModifierBlockType.values()) {
			ModifierBlock modifierBlock = modifierBlockType.getModifierBlock();


			if (checkApplies(below, belowType, modifierBlockType, modifierBlock)) {
				MiniGolfBallModifierBlockEvent modifierBlockEvent = new MiniGolfBallModifierBlockEvent(golfBall, modifierBlockType);
				if (modifierBlockEvent.callEvent()) {
					modifierBlock.handleRoll(golfBall);
					break;
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

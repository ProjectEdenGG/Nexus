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
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.block.Block;
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
  - whistle resets ball location slightly higher each time
  - tbd
*/

public class MiniGolf extends Feature {
	@Getter
	private static final double maxVelocity = 1.5;
	@Getter
	private static final double minVelocity = 0.01;
	@Getter
	private static final double floorOffset = 0.05;
	@Getter
	private static final String holeRegionRegex = ".*minigolf_hole_[\\d]+$";

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
				if (!user.isOnline() || user.getGolfBall() == null || !user.getGolfBall().isAlive())
					continue;

				Player player = user.getOnlinePlayer();
				if (!MiniGolfUtils.isClub(ItemUtils.getTool(player)))
					continue;

				float amount = player.getPing() < 200 ? 0.04F : 0.02F;

				float exp = powerMap.getOrDefault(user.getUuid(), .0F);
				exp += amount;
				if (exp > 1.00)
					exp = 0.0F;

				powerMap.put(user.getUuid(), exp);

				player.sendExperienceChange(exp, 0);
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
					golfBall.remove();
					continue;
				}

//				Location location = ball.getLocation();
//				if (golfBall.getLastLocation().equals(location))
//					continue;

				MiniGolfBallMoveEvent ballMoveEvent = new MiniGolfBallMoveEvent(golfBall, golfBall.getLastLocation(), ball.getLocation());
				if (!ballMoveEvent.callEvent()) {
					ball.teleportAsync(golfBall.getLastLocation());
					ball.setVelocity(ball.getVelocity());
				}

				Block below = golfBall.getBlockBelow();
				Material belowType = below.getType();

				for (ModifierBlockType modifierBlockType : ModifierBlockType.values()) {
					ModifierBlock modifierBlock = modifierBlockType.getModifierBlock();
					if (modifierBlockType.equals(ModifierBlockType.DEFAULT) || modifierBlock.getMaterials().contains(belowType)) {
						MiniGolfBallModifierBlockEvent modifierBlockEvent = new MiniGolfBallModifierBlockEvent(golfBall, modifierBlockType);
						if (modifierBlockEvent.callEvent()) {
							modifierBlock.handleRoll(golfBall);
							break;
						}
					}
				}
			}
		});
	}

}
